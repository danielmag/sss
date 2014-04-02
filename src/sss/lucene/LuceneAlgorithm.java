package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
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
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import sss.dialog.SimpleQA;
import sss.texttools.Lemmatizer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LuceneAlgorithm {
    private Analyzer analyzer;
    private Lemmatizer lemmatizer;
    private Directory index;
    private IndexSearcher searcher;

    public LuceneAlgorithm(String pathOfIndex, String pathOfCorpus, String language, Lemmatizer lemmatizer) throws IOException {
        this.lemmatizer = lemmatizer;
        try {
            initAnalyzer(language);
            this.index = createIndex(analyzer, pathOfIndex, pathOfCorpus);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IndexReader reader = DirectoryReader.open(index);
        this.searcher = new IndexSearcher(reader);
    }

    public LuceneAlgorithm(String pathOfIndex, String language, Lemmatizer lemmatizer) throws IOException {
        this.lemmatizer = lemmatizer;
        File indexDirec = new File(pathOfIndex);
        System.out.println(pathOfIndex);
        try {
            initAnalyzer(language);
            this.index = MMapDirectory.open(indexDirec);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IndexReader reader = DirectoryReader.open(index);
        this.searcher = new IndexSearcher(reader);
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
        Directory index = MMapDirectory.open(indexDirec);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);

        IndexWriter writer = new IndexWriter(index, config);
        writer.deleteAll(); //delete previous lucene files

        String subId;
        String question;
        String answer;

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
            long totalLines = countLines(file.toPath());
            long lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if ((lineNum % 10000) == 0) {
                    System.out.println(getPercentage(lineNum, totalLines));
                }
                if (line.trim().length() == 0) {
                    continue;
                }
                String temp = line; //TODO create neat class for this
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

                String lemmatizedAnswer = this.lemmatizer.getLemmatizedString(answer).toLowerCase();
                String lemmatizedQuestion = this.lemmatizer.getLemmatizedString(question).toLowerCase();

                //TODO: check if removing answers that end with a question mark might be useful...
                SimpleQA simpleQA;
                if (dialogId == previousDialogId + 1) {
                    simpleQA = new SimpleQA(internalId, question, answer, lemmatizedQuestion, lemmatizedAnswer, diff);
                } else {
                    simpleQA = new SimpleQA(-1, question, answer, lemmatizedQuestion, lemmatizedAnswer, diff);
                }
                db.store(simpleQA);
                internalId = db.ext().getID(simpleQA);
                previousDialogId = dialogId;
                addDoc(writer, lemmatizedQuestion, String.valueOf(internalId));
            }
            System.out.println();
        }
        writer.close();
        db.close();
        return index;
    }

    private String getPercentage(long partial, long total) {
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        return defaultFormat.format(partial / (double) total);
    }

    private String getSubstringAfterHyphen(String temp) {
        return temp.substring(temp.indexOf('-') + 2, temp.length());
    }

    private long countLines(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath, Charset.defaultCharset())) {
            return lines.count();
        }
    }

    public List<Document> search(String inputQuestion, int hitsPerPage) throws IOException, ParseException {
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        Query q = new QueryParser(Version.LUCENE_43, "question", this.analyzer).parse(inputQuestion);
        this.searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        ArrayList<Document> docList = new ArrayList<>();
        for (ScoreDoc scoreDoc : hits) {
            int docId = scoreDoc.doc;
            Document d = this.searcher.doc(docId);
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