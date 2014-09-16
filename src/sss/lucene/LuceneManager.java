package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;
import sss.dialog.BasicQA;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.evaluator.*;
import sss.distance.algorithms.DistanceAlgorithmFactory;
import sss.evaluatedtas.Reader;
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
import java.util.Date;
import java.util.List;

public class LuceneManager {
    public static final String ANALYZER_PROPERTIES = "tokenize, ssplit, pos, lemma";
    public static final String DB4OFILENAME = Paths.get("").toAbsolutePath().toString() + "/db.db4o";
    public static ObjectContainer db;
    private ConfigParser configParser;
    private LuceneAlgorithm luceneAlgorithm;
    private List<Normalizer> normalizers;
    private List<QaScorer> qaScorers;
    public static List<BasicQA> CONVERSATION;

    public LuceneManager() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        this.configParser = new ConfigParser("./resources/config/config.xml");
        String pathOfIndex = configParser.getLuceneIndexPath();
        String language = this.configParser.getLanguage();
        this.normalizers = (new NormalizerFactory()).createNormalizers(this.configParser.getNormalizations());
        this.qaScorers = (new QaScorerFactory().createQaScorers(this.configParser.getQaScorers(),
                this.configParser.getStopWordsLocation(),
                this.normalizers,
                new DistanceAlgorithmFactory().getDistanceAlgorithm(this.configParser.getDistanceAlgorithm())));
        if (configParser.usePreviouslyCreatedIndex()) {
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, language, normalizers);
        } else {
            String pathOfCorpus = configParser.getCorpusPath();
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, pathOfCorpus, language, normalizers);
        }
        this.db = Db4oEmbedded.openFile(LuceneManager.DB4OFILENAME);
        this.CONVERSATION = new ArrayList<>(); //should be a stack...
    }

    public String getAnswer(String question) throws IOException, ParseException, ClassNotFoundException {
        String normalizedQuestion = Normalizer.applyNormalizations(question, this.normalizers);
        Main.printDebug("Normalized question: " + normalizedQuestion);

        if (!CONVERSATION.isEmpty()) {
            BasicQA basicQA = CONVERSATION.get(CONVERSATION.size()-1);
            storeDialogue(question, normalizedQuestion, basicQA.getAnswer(), basicQA.getNormalizedAnswer());
        }

        Main.printDebug("Retrieving Lucene results...");
        List<Document> luceneDocs = this.luceneAlgorithm.search(normalizedQuestion, this.configParser.getHitsPerQuery());

        Main.printDebug("Retrieving QA's from database...");
        List<QA> searchedResults = loadLuceneResults(luceneDocs);

        Main.printDebug("Scoring the QA's...");
        List<QA> scoredQas = scoreLuceneResults(normalizedQuestion, searchedResults);

        QA answer = getBestAnswer(question, scoredQas);
        storeDialogue(answer.getAnswer(), answer.getNormalizedAnswer(), question, normalizedQuestion);

        Main.printDebug("Best answer score: " + answer.getScore());
        return answer.getAnswer();
    }

    private List<QA> loadLuceneResults(List<Document> docList) {
        List<QA> qas = new ArrayList<>();
        for (Document d : docList) {
            String qaId = d.get("answer");
            SimpleQA simpleQA = getSimpleQA(Long.parseLong(qaId));
            QA qa = new QA(simpleQA.getPreviousQA(),
                    simpleQA.getQuestion(), simpleQA.getAnswer(),
                    simpleQA.getNormalizedQuestion(), simpleQA.getNormalizedAnswer(),
                    simpleQA.getDiff());
            qas.add(qa);
        }
        return qas;
    }

    public static SimpleQA getSimpleQA(long qaId) {
        SimpleQA simpleQA = db.ext().getByID(qaId);
        db.activate(simpleQA, 1);
        return simpleQA;
    }

    private List<QA> scoreLuceneResults(String question, List<QA> searchedResults) throws IOException {
        for (QaScorer qaScorer : this.qaScorers) { //TODO deal with named entities
            qaScorer.score(question, searchedResults);
        }
        return searchedResults;
    }

    private QA getBestAnswer(String question, List<QA> scoredQas) throws IOException {
        if (scoredQas.size() == 0 || scoredQas == null) {
            return new QA(0, question, this.configParser.getNoAnswerFoundMsg(), null, null, 0);
        }
        if (Main.SORT) {
            Collections.sort(scoredQas);
            for (int i = 0; i < Main.N_ANSWERS; i++) {
                QA qa = scoredQas.get(i);
                Main.printDebug("" + (i + 1));
                Main.printDebug("T - " + qa.getQuestion());
                Main.printDebug("A - " + qa.getAnswer());
                Main.printDebug("S - " + qa.getScore());
                Main.printDebug("");
            }
            return scoredQas.get(0);
        } else {
            if (Main.LEARN_TO_RANK) {
                Reader reader = new Reader("C:\\Users\\Daniel\\Desktop\\Evaluation\\Eu\\eval.txt");
                for (QA qa : scoredQas) {
//                    System.out.println("\tA - " + qa.getAnswer());
                    String eval = reader.getEvaluatedTAs().getAnswerEvaluation(question, qa.getAnswer());
                    System.out.print(eval + "\t" + "qid:" + Main.qid + "\t");
                    qa.printScores();
                    System.out.println();
                }
                return new QA(0, question, this.configParser.getNoAnswerFoundMsg(), null, null, 0);
            } else {
                double max = 0;
                QA bestQa = null;
                for (QA qa : scoredQas) {
                    Main.printDebug("T - " + qa.getQuestion());
                    Main.printDebug("A - " + qa.getAnswer());
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
    }

    private void storeDialogue(String answer, String normalizedAnswer, String question, String normalizedQuestion) {
        CONVERSATION.add(new BasicQA(question, answer, normalizedQuestion, normalizedAnswer));
        try {
            FileWriter x = new FileWriter(this.configParser.getLogPath(), true);
            String localDateTime = new Date().toString();
            x.write("I - " + question + "\n" +
                    "R - " + answer + "\n" +
                    "T - " + localDateTime + "\n\n");
            x.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}