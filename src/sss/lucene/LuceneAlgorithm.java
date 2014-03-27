package sss.lucene;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import sss.dialog.SimpleQA;
import sss.dialog.WholeDialog;
import sss.texttools.TextAnalyzer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuceneAlgorithm {

    public static final String DELIMITER = " nuncamaisistoaparecenumalegenda "; //careful if you use characters that need to be escaped!
    private Analyzer analyzer;

    private Directory index = null;

    public LuceneAlgorithm(String pathOfIndex, String pathOfCorpus, String language) {
        try {
            initAnalyzer(language);
            index = createIndex(analyzer, pathOfIndex, pathOfCorpus);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public LuceneAlgorithm(String pathOfIndex, String language) {
        File indexDirec = new File(pathOfIndex);
        System.out.println(pathOfIndex);
        try {
            initAnalyzer(language);
            index = FSDirectory.open(indexDirec);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAnalyzer(String language) {
        if (language.equalsIgnoreCase("portuguese")) {
            analyzer = new SnowballAnalyzer(Version.LUCENE_43, "Portuguese");
        } else if (language.equalsIgnoreCase("english")) {
            analyzer = new StandardAnalyzer(Version.LUCENE_43);
        }
    }


    // 1. create the index
    private Directory createIndex(Analyzer analyzer, String indexDir, String corpusDir) throws IOException {
        File indexDirec = new File(indexDir);
        FSDirectory index = FSDirectory.open(indexDirec);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);

        IndexWriter writer = new IndexWriter(index, config);
        writer.deleteAll(); //delete previous lucene files

        deleteAllSerializedObjects();

        String id;
        String question;
        String answer;
        TextAnalyzer textAnalyzer = new TextAnalyzer(LuceneManager.ANALYZER_PROPERTIES);

        File dir = new File(LuceneManager.SERIALIZED_OBJECTS_LOCATION);
        dir.mkdirs();
        File f = new File(corpusDir);
        File[] files = f.listFiles();
        for (File file : files) {
            System.out.println("Creating indexes for " + file.getName() + "...");
            BufferedReader reader = new BufferedReader(new FileReader(file.getCanonicalPath()));
            String line;
            int dialogNumber = 0;
            int previousDialogId = -1;
            WholeDialog wholeDialog = new WholeDialog();
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String temp = line;
                assert (temp.startsWith("SubId"));
                id = getSubstringAfterHyphen(temp);
                temp = reader.readLine();
                assert (temp.startsWith("DialogId"));
                int dialogId = Integer.parseInt(getSubstringAfterHyphen(temp));

                temp = reader.readLine();
                assert (temp.startsWith("Diff"));
                long diff = Long.parseLong(getSubstringAfterHyphen(temp));

                temp = reader.readLine();
                assert (temp.startsWith("Q"));
                question = getSubstringAfterHyphen(temp); //assumes the corpus does not have empty questions

                temp = reader.readLine();
                assert (temp.startsWith("A"));
                answer = getSubstringAfterHyphen(temp); //assumes the corpus does not have empty answers
                answer = answer.trim();

                List<CoreMap> answerSentences = textAnalyzer.analyze(answer);
                List<CoreMap> questionSentences = textAnalyzer.analyze(answer);
                String lemmatizedAnswer = getLemmatizedString(answerSentences);
                String lemmatizedQuestion = getLemmatizedString(questionSentences);

                //TODO: check if removing answers that end with a question mark might be useful...

                if (dialogId == previousDialogId + 1) {
                    wholeDialog.addSimpleQA(new SimpleQA(question, answer, lemmatizedQuestion, lemmatizedAnswer, questionSentences, answerSentences, diff));
                    if (dialogId > 0) {
                        assert (wholeDialog.getSimpleQA(dialogId).getQuestion().trim().equals(wholeDialog.getSimpleQA(dialogId - 1).getAnswer().trim()));
                    }
                } else {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                            new FileOutputStream(dir.getCanonicalPath() + "/" + dialogNumber + ".ser"));
                    objectOutputStream.writeObject(wholeDialog);
                    wholeDialog = new WholeDialog();
                    wholeDialog.addSimpleQA(new SimpleQA(question, answer, lemmatizedQuestion, lemmatizedAnswer, questionSentences, answerSentences, diff));
                    dialogNumber++;
                }
                previousDialogId = dialogId;
                addDoc(writer, lemmatizedQuestion, lemmatizedAnswer + DELIMITER + dialogNumber + DELIMITER + dialogId);
            }
        }
        writer.close();

        return index;
    }

    private void deleteAllSerializedObjects() {
        System.out.println("Cleaning old serialized objects...");
        File dir = new File(LuceneManager.SERIALIZED_OBJECTS_LOCATION);
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    private String getSubstringAfterHyphen(String temp) {
        return temp.substring(temp.indexOf('-') + 2, temp.length());
    }

    private String getLemmatizedString(List<CoreMap> sentences) {
        String lemmatizedAnswer = "";
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (lemma.length() == 1 && !Character.isLetterOrDigit(lemma.charAt(0))) {
                    continue;
                }
                lemmatizedAnswer += lemma + " ";
            }
        }
        return lemmatizedAnswer;
    }

    public List<Document> search(String inputQuestion, int hitsPerPage) throws IOException, ParseException {
        Query q = new QueryParser(Version.LUCENE_43, "question", analyzer).parse(inputQuestion);
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        ArrayList<Document> docList = new ArrayList<>();
        for (ScoreDoc scoreDoc : hits) {
            int docId = scoreDoc.doc;
            Document d = searcher.doc(docId);
            docList.add(d);
        }

        return docList;
    }

    private void addDoc(IndexWriter w, String question, String answer) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("question", question, Field.Store.YES));
        doc.add(new TextField("answer", answer, Field.Store.YES));
        w.addDocument(doc);
    }

    private CharArraySet getStopWords() throws IOException {
        Pattern pattern = Pattern.compile("^[\\p{L}]+");
        List<String> stopWords = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(
                        new File("./resources/portuguese_stop_smaller.txt")), StandardCharsets.ISO_8859_1
        ));
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                stopWords.add(matcher.group());
            }
        }
        return StopFilter.makeStopSet(Version.LUCENE_43, stopWords, true);
    }


}