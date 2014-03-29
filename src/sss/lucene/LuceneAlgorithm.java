package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
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
import sss.texttools.Lemmatizer;
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

    private Directory createIndex(Analyzer analyzer, String indexDir, String corpusDir) throws IOException {
        File indexDirec = new File(indexDir);
        FSDirectory index = FSDirectory.open(indexDirec);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);

        IndexWriter writer = new IndexWriter(index, config);
        writer.deleteAll(); //delete previous lucene files

        String subId;
        String question;
        String answer;
        TextAnalyzer textAnalyzer = new TextAnalyzer(LuceneManager.ANALYZER_PROPERTIES);
        Lemmatizer lemmatizer = new Lemmatizer();

        EmbeddedConfiguration db4oConfig = Db4oEmbedded.newConfiguration();
        db4oConfig.file().blockSize(8);
        ObjectContainer db = Db4oEmbedded.openFile(db4oConfig, LuceneManager.DB4OFILENAME);
        long internalId = -1;

        File f = new File(corpusDir);
        File[] files = f.listFiles();
        for (File file : files) {
            System.out.println("Creating lucene indexes and database for " + file.getName() + "...");
            BufferedReader reader = new BufferedReader(new FileReader(file.getCanonicalPath()));
            String line;
            int previousDialogId = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String temp = line;
                assert (temp.startsWith("SubId"));
                subId = getSubstringAfterHyphen(temp);
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
                List<CoreMap> questionSentences = textAnalyzer.analyze(question);
                String lemmatizedAnswer = lemmatizer.getLemmatizedString(answerSentences);
                String lemmatizedQuestion = lemmatizer.getLemmatizedString(questionSentences);

                //TODO: check if removing answers that end with a question mark might be useful...
                SimpleQA simpleQA;
                if (dialogId == previousDialogId + 1) {
                    simpleQA = new SimpleQA(internalId, question, answer, lemmatizedQuestion, lemmatizedAnswer, questionSentences, answerSentences, diff);
                } else {
                    simpleQA = new SimpleQA(-1, question, answer, lemmatizedQuestion, lemmatizedAnswer, questionSentences, answerSentences, diff);
                }
                db.store(simpleQA);
                internalId = db.ext().getID(simpleQA);
                previousDialogId = dialogId;
                addDoc(writer, lemmatizedQuestion, String.valueOf(internalId));
            }
            System.out.println();
        }
        writer.close();

        return index;
    }

    private String getSubstringAfterHyphen(String temp) {
        return temp.substring(temp.indexOf('-') + 2, temp.length());
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