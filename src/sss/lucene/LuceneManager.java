package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.evaluator.QaScorer;
import sss.dialog.evaluator.QaScorerFactory;
import sss.main.Main;
import sss.resources.ConfigParser;
import sss.texttools.normalizer.Normalizer;
import sss.texttools.normalizer.NormalizerFactory;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
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
        this.normalizers = (new NormalizerFactory()).createNormalizers(this.configParser.getNormalizations());
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
        Main.printDebug("Normalized question: " + normalizedQuestion);
        Main.printDebug("Retrieving Lucene results...");
        List<Document> luceneDocs = this.luceneAlgorithm.search(normalizedQuestion, this.configParser.getHitsPerQuery());
        Main.printDebug("Retrieving QA's from database...");
        List<QA> searchedResults = loadLuceneResults(luceneDocs);
        Main.printDebug("Scoring the QA's...");
        List<QA> scoredQas = scoreLuceneResults(normalizedQuestion, searchedResults);
        QA answer = getBestAnswer(question, scoredQas);
        addGivenAnswer(answer);
        Main.printDebug("Best answer score: " + answer.getScore());
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

    private SimpleQA getSimpleQA(final long qaId) {
        List<SimpleQA> simpleQAs = this.db.query(new Predicate<SimpleQA>() {
            public boolean match(SimpleQA simpleQA) {
                return simpleQA.getUniqueIdentifier() == qaId;
            }
        });
        assert simpleQAs.size() == 1;
        return simpleQAs.get(0);
    }

    private List<QA> scoreLuceneResults(String question, List<QA> searchedResults) throws IOException {
        for (QaScorer qaScorer : this.qaScorers) { //TODO deal with named entities
            qaScorer.score(question, searchedResults);
        }
        return searchedResults;
    }

    private QA getBestAnswer(String question, List<QA> scoredQas) {
        if (scoredQas.size() == 0 || scoredQas == null) {
            return new QA(question, this.configParser.getNoAnswerFoundMsg(), null, null, 0);
        }
        if (Main.SORT) {
            Collections.sort(scoredQas);
            for (int i = 0; i < Main.N_ANSWERS; i++) {
                QA qa = scoredQas.get(i);
                Main.printDebug("" + (i + 1));
                Main.printDebug("I - " + qa.getQuestion());
                Main.printDebug("R - " + qa.getAnswer());
                Main.printDebug("S - " + qa.getScore());
                Main.printDebug("");
            }
            return scoredQas.get(0);
        } else {
            double max = 0;
            QA bestQa = null;
            for (QA qa : scoredQas) {
                Main.printDebug("I - " + qa.getQuestion());
                Main.printDebug("R - " + qa.getAnswer());
                Main.printDebug("S - " + qa.getScore());
                Main.printDebug("");
                if (qa.getScore() > max) {
                    max = qa.getScore();
                    bestQa = qa;
                }
            }
            return bestQa;
        }
    }

    private void addGivenAnswer(QA qa) {
        try {
            FileWriter x = new FileWriter(this.configParser.getLogPath(), true);
            String localDateTime = "now";
            x.write("I - " + qa.getQuestion() + "\n" +
                    "R - " + qa.getAnswer() + "\n" +
                    "T - " + localDateTime + "\n\n");
            x.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}