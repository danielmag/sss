package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import org.apache.lucene.util.Version;
import sss.main.Main;
import sss.resources.CorpusReader;
import sss.resources.SubtitleCorpusReader;
import sss.texttools.normalizer.Normalizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneAlgorithm {
    private Analyzer analyzer;
    private List<Normalizer> normalizers;
    private Directory index;
    private IndexSearcher searcher;

    public LuceneAlgorithm(String pathOfIndex, String pathOfCorpus, String language, List<Normalizer> normalizers) throws IOException {
        this.normalizers = normalizers;
        try {
            initAnalyzer(language);
            this.index = createIndex(analyzer, pathOfIndex, pathOfCorpus);
        } catch (IOException e) {
            e.printStackTrace();
        }
        IndexReader reader = DirectoryReader.open(index);
        this.searcher = new IndexSearcher(reader);
    }

    public LuceneAlgorithm(String pathOfIndex, String language, List<Normalizer> normalizers) throws IOException {
        this.normalizers = normalizers;
        File indexDirec = new File(pathOfIndex);
        Main.printDebug(pathOfIndex);
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

        File dbFile = new File(LuceneManager.DB4OFILENAME);
        dbFile.delete();

        EmbeddedConfiguration db4oConfig = Db4oEmbedded.newConfiguration();
        db4oConfig.file().blockSize(8);
        db4oConfig.file().lockDatabaseFile(false);
        ObjectContainer db = Db4oEmbedded.openFile(db4oConfig, LuceneManager.DB4OFILENAME);

        File f = new File(corpusDir);
        File[] files = f.listFiles();
        CorpusReader corpusReader = new SubtitleCorpusReader();
        corpusReader.read(writer, db, files, this.normalizers);
        db4oConfig.file().readOnly(true);
	writer.close();
        db.close();
        return index;
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
}
