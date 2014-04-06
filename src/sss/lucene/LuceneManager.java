package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.evaluator.*;
import sss.resources.ConfigParser;
import sss.texttools.normalizer.EnglishLemmatizer;
import sss.texttools.normalizer.Normalizer;
import sss.texttools.normalizer.SimpleNormalizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LuceneManager {
    public static final String ANALYZER_PROPERTIES = "tokenize, ssplit, pos, lemma";
    protected static final String DB4OFILENAME = Paths.get("").toAbsolutePath().toString() + "/db.db4o";
    private ObjectContainer db;
    private ConfigParser configParser;
    private LuceneAlgorithm luceneAlgorithm;
    private List<Normalizer> normalizers;
    private List<QaScorer> qaScorers;

    public LuceneManager() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        this.configParser = new ConfigParser("./resources/config/config.xml");
        String pathOfIndex = configParser.getLuceneIndexPath();
        String language = this.configParser.getLanguage();
        List<Normalizer> normalizers = new ArrayList<>();
        normalizers.add(new EnglishLemmatizer()); //TODO config
        normalizers.add(new SimpleNormalizer());
        this.normalizers = normalizers;
        this.qaScorers = (new QaScorerFactory().createQaScorers(this.configParser.getQaScorers(),
                this.configParser.getStopWordsLocation(),
                this.normalizers));
        if (configParser.usePreviouslyCreatedIndex()) {
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, language, normalizers);
        } else {
            String pathOfCorpus = configParser.getCorpusPath();
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, pathOfCorpus, language, normalizers);
        }
        this.db = Db4oEmbedded.openFile(LuceneManager.DB4OFILENAME);
    }

    public String getAnswer(String question) throws IOException, ParseException, ClassNotFoundException {
        String normalizedQuestion = Normalizer.applyNormalizations(question, this.normalizers);
        System.out.println("Normalized question: " + normalizedQuestion); //TODO debug
        System.out.println("Retrieving Lucene results...");
        List<Document> luceneDocs = this.luceneAlgorithm.search(normalizedQuestion, this.configParser.getHitsPerQuery());
        System.out.println("Retrieving QA's from database...");
        List<QA> searchedResults = loadLuceneResults(luceneDocs);
        System.out.println("Scoring the QA's...");
        List<QA> scoredQas = scoreLuceneResults(normalizedQuestion, searchedResults);
        QA answer = getBestAnswer(question, scoredQas);
        addGivenAnswer(answer);
        System.out.println("Best answer score: " + answer.getScore());
        return answer.getAnswer();
    }

    private List<QA> loadLuceneResults(List<Document> docList) {
        List<QA> qas = new ArrayList<>();
        for (Document d : docList) {
            String qaId = d.get("answer");
            SimpleQA simpleQA = getSimpleQA(Long.parseLong(qaId));
            QA qa = new QA(simpleQA.getQuestion(), simpleQA.getAnswer(),
                    simpleQA.getNormalizedQuestion(), simpleQA.getNormalizedAnswer(),
                    simpleQA.getDiff());
            qas.add(qa);
        }
        return qas;
    }

    private SimpleQA getSimpleQA(long qaId) {
        SimpleQA simpleQA = this.db.ext().getByID(qaId);
        db.activate(simpleQA, 1);
        return simpleQA;
    }

    private List<QA> scoreLuceneResults(String question, List<QA> searchedResults) throws IOException {
        for (QaScorer qaScorer : this.qaScorers) {
            qaScorer.score(question, searchedResults);
        }
        return searchedResults;
    }

    private QA getBestAnswer(String question, List<QA> scoredQas) {
        if (scoredQas.size() == 0 || scoredQas == null) {
            return new QA(question, this.configParser.getNoAnswerFoundMsg(), null, null, 0);
        }
        double max = 0;
        QA bestQa = null;
        for (QA qa : scoredQas) {
            System.out.println("I - " + qa.getQuestion());
            System.out.println("R - " + qa.getAnswer());
            System.out.println("S - " + qa.getScore());
            System.out.println();
            if (qa.getScore() > max) {
                max = qa.getScore();
                bestQa = qa;
            }
        }
        return bestQa;
    }

    private void addGivenAnswer(QA qa) { //TODO add date and time
        try {
            FileWriter x = new FileWriter(this.configParser.getLogPath(), true);
            x.write("Q - " + qa.getQuestion() + "\t" + "A - " + qa.getAnswer() + "\n");
            x.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}