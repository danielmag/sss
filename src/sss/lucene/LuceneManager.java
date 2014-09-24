package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;
import ptstemmer.exceptions.PTStemmerException;
import sss.dialog.BasicQA;
import sss.dialog.Conversation;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.evaluator.Evaluator;
import sss.dialog.evaluator.l2r.LearnToRankEvaluator;
import sss.dialog.evaluator.qascorer.QaScorerFactory;
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
import java.util.*;

public class LuceneManager {
    public static final String ANALYZER_PROPERTIES = "tokenize, ssplit, pos, lemma";
    public static final String DB4OFILENAME = Paths.get("").toAbsolutePath().toString() + "/db.db4o";
    public static ObjectContainer db;
    private ConfigParser configParser;
    private LuceneAlgorithm luceneAlgorithm;
    private List<Normalizer> normalizers;
    private List<Evaluator> evaluators;
    public static Conversation CONVERSATION;

    public LuceneManager() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException, PTStemmerException {
        this.configParser = new ConfigParser("./resources/config/config.xml");
        String pathOfIndex = configParser.getLuceneIndexPath();
        String language = this.configParser.getLanguage();
        this.normalizers = (new NormalizerFactory()).createNormalizers(this.configParser.getNormalizations());
        if (configParser.getEvaluationName().equalsIgnoreCase("l2r")) {
            ArrayList<String> qaScorers = new ArrayList<>();
            qaScorers.add("AnswerFrequency,0");
            qaScorers.add("AnswerSimilarityToUserQuestion,0");
            qaScorers.add("QuestionSimilarityToUserQuestion,0");
            qaScorers.add("SimpleTimeDifference,0");
            List<Evaluator> evaluatorList = new ArrayList<>();
            evaluatorList.add(new LearnToRankEvaluator(configParser.getModelPath(), (new QaScorerFactory().createQaScorers(qaScorers,
                    this.configParser.getStopWordsLocation(),
                    this.normalizers,
                    new DistanceAlgorithmFactory().getDistanceAlgorithm(this.configParser.getDistanceAlgorithm())))));
            this.evaluators = evaluatorList;
        } else {
            this.evaluators = (new QaScorerFactory().createQaScorers(this.configParser.getQaScorers(),
                    this.configParser.getStopWordsLocation(),
                    this.normalizers,
                    new DistanceAlgorithmFactory().getDistanceAlgorithm(this.configParser.getDistanceAlgorithm())));
        }
        if (configParser.usePreviouslyCreatedIndex()) {
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, language, normalizers);
        } else {
            String pathOfCorpus = configParser.getCorpusPath();
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, pathOfCorpus, language, normalizers);
        }
        this.db = Db4oEmbedded.openFile(LuceneManager.DB4OFILENAME);
        this.CONVERSATION = new Conversation();
    }

    public String getAnswer(String question) throws IOException, ParseException {
        if (question == null || question.trim().isEmpty()) {
            return getNoReplyMessage();
        }
        String normalizedQuestion = Normalizer.applyNormalizations(question, this.normalizers);
        Main.printDebug("Normalized question: " + normalizedQuestion);

        if (!CONVERSATION.isEmpty()) {
            BasicQA basicQA = CONVERSATION.getLastQA();
            storeDialogue(question, normalizedQuestion, basicQA.getAnswer(), basicQA.getNormalizedAnswer());
        }

        Main.printDebug("Retrieving Lucene results...");
        List<Document> luceneDocs = this.luceneAlgorithm.search(normalizedQuestion, this.configParser.getHitsPerQuery());

        Main.printDebug("Retrieving QA's from database...");
        List<QA> searchedResults = loadLuceneResults(luceneDocs);

        Main.printDebug("Scoring the QA's...");
        List<QA> scoredQas = scoreLuceneResults(normalizedQuestion, searchedResults);

        QA answer = getBestAnswer(question, scoredQas, normalizedQuestion);
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
        for (Evaluator qaScorer : this.evaluators) { //TODO deal with named entities
            qaScorer.score(question, searchedResults);
        }
        return searchedResults;
    }

    private QA getBestAnswer(String question, List<QA> scoredQas, String normalizedQuestion) throws IOException {
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
                    System.out.print(eval + " " + "qid:" + Main.qid + " ");
                    qa.printScores();
                    for (String q : Main.questionHeadWords) {
                        if (q.equalsIgnoreCase(normalizedQuestion.split("\\s+")[0])) {
                            System.out.print(" 1");
                        } else {
                            System.out.print(" 0");
                        }
                    }
                    for (String a : Main.answerHeadWords) {
                        if (a.equalsIgnoreCase(qa.getAnswerListNormalized().get(0))) {
                            System.out.print(" 1");
                        } else {
                            System.out.print(" 0");
                        }
                    }
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
        CONVERSATION.addQA(new BasicQA(question, answer, normalizedQuestion, normalizedAnswer));
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

    public String getNoReplyMessage() {
        return configParser.getNoAnswerFoundMsg();
    }

}