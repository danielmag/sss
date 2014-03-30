package sss.lucene;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.evaluator.*;
import sss.resources.ConfigParser;
import sss.texttools.Lemmatizer;

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
    private Lemmatizer lemmatizer;

    public LuceneManager() throws IOException {
        this.configParser = new ConfigParser("./resources/config/config.xml");
        String pathOfIndex = configParser.getLuceneIndexPath();
        String language = this.configParser.getLanguage();
        this.lemmatizer = new Lemmatizer();
        if (configParser.isUsePreviouslyCreatedIndex()) {
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, language, this.lemmatizer);
        } else {
            String pathOfCorpus = configParser.getCorpusPath();
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, pathOfCorpus, language, this.lemmatizer);
        }
        this.db = Db4oEmbedded.openFile(LuceneManager.DB4OFILENAME);
    }

    public String getAnswer(String question) throws IOException, ParseException, ClassNotFoundException {
        String lemmatizedQuestion = this.lemmatizer.getLemmatizedString(question).toLowerCase();
        System.out.println("Lemmatized question: " + lemmatizedQuestion);
        System.out.println("Retrieving Lucene results...");
        List<Document> luceneDocs = this.luceneAlgorithm.search(lemmatizedQuestion, this.configParser.getHitsPerQuery());
        System.out.println("Retrieving QA's from database...");
        List<QA> searchedResults = loadLuceneResults(luceneDocs);
        System.out.println("Scoring the QA's...");
        List<QA> scoredQas = scoreLuceneResults(lemmatizedQuestion, searchedResults);
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
                    simpleQA.getLemmatizedQuestion(), simpleQA.getLemmatizedAnswer(),
                    simpleQA.getDiff());
            qas.add(qa);
        }
        return qas;
    }

    private SimpleQA getSimpleQA(long qaId) {
        SimpleQA simpleQA = db.ext().getByID(qaId);
        db.activate(simpleQA, 1);
        return simpleQA;
    }

    private List<QA> scoreLuceneResults(String question, List<QA> searchedResults) {
        List<QaScorer> qaScorers = new ArrayList<>();
        qaScorers.add(new AnswerFrequency(0.3)); //TODO CONFIG!!!!!
        qaScorers.add(new QuestionSimilarityToUserQuestion(0.4));
        qaScorers.add(new SimpleTimeDifference(0.1));
        qaScorers.add(new AnswerSimilarityToUserQuestion(0.2));
        for (QaScorer qaScorer : qaScorers) {
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
            System.out.println("Q - " + qa.getQuestionLemmatized());
            System.out.println("A - " + qa.getAnswerLemmatized());
            System.out.println("S - " + qa.getScore());
            System.out.println();
            if (qa.getScore() > max) {
                max = qa.getScore();
                bestQa = qa;
            }
        }
        return bestQa;
    }

    private void addGivenAnswer(QA qa) {
        try {
            FileWriter x = new FileWriter(this.configParser.getLogPath(), true);
            x.write("Q - " + qa.getQuestion() + "\t" + "A - " + qa.getAnswer() + "\n");
            x.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}