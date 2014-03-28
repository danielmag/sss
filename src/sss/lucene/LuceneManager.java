package sss.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.evaluator.AnswerFrequency;
import sss.dialog.evaluator.QaScorer;
import sss.dialog.evaluator.SimilarityToUserQuestion;
import sss.dialog.evaluator.SimpleTimeDifference;
import sss.resources.ConfigParser;
import sss.texttools.Lemmatizer;
import sss.texttools.TextAnalyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LuceneManager {
    protected static final String SERIALIZED_OBJECTS_LOCATION = "./resources/serializedObjects";
    protected static final String ANALYZER_PROPERTIES = "tokenize, ssplit, pos, lemma";
    private ConfigParser configParser;
    private LuceneAlgorithm luceneAlgorithm;

    public LuceneManager() {
        this.configParser = new ConfigParser("./resources/config/config.xml");
        String pathOfIndex = configParser.getLuceneIndexPath();
        String language = this.configParser.getLanguage();
        if (configParser.isUsePreviouslyCreatedIndex()) {
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, language);
        } else {
            String pathOfCorpus = configParser.getCorpusPath();
            luceneAlgorithm = new LuceneAlgorithm(pathOfIndex, pathOfCorpus, language);
        }
    }

    public String getAnswer(String question) throws IOException, ParseException, ClassNotFoundException {
        TextAnalyzer textAnalyzer = new TextAnalyzer(LuceneManager.ANALYZER_PROPERTIES);
        Lemmatizer lemmatizer = new Lemmatizer();
        String lemmatizedQuestion = lemmatizer.getLemmatizedString(textAnalyzer.analyze(question));
        System.out.println(lemmatizedQuestion);
        System.out.println("AAAAAAAA");
        List<Document> luceneDocs = this.luceneAlgorithm.search(lemmatizedQuestion, this.configParser.getHitsPerQuery());
        System.out.println("BBBBBBBBBB");
        List<QA> searchedResults = loadLuceneResults(luceneDocs);
        System.out.println("CCCCCCCC");
        List<QA> scoredQas = scoreLuceneResults(lemmatizedQuestion, searchedResults);
        System.out.println("DDDDDDDDD");
        QA answer = getBestAnswer(question, scoredQas);
        addGivenAnswer(answer);
        return answer.getAnswer();
    }

    private List<QA> loadLuceneResults(List<Document> docList) throws IOException, ClassNotFoundException {
        List<QA> qas = new ArrayList<>();
        for (Document d : docList) {
            String qaId = d.get("answer");
            SimpleQA simpleQA = deserializeSimpleQA(qaId);
            QA qa = new QA(simpleQA.getQuestion(), simpleQA.getAnswer(),
                    simpleQA.getLemmatizedQuestion(), simpleQA.getLemmatizedAnswer(),
                    simpleQA.getDiff());
            qas.add(qa);
        }
        return qas;
    }

    private SimpleQA deserializeSimpleQA(String simpleQAFile) throws IOException, ClassNotFoundException {
        File dir = new File(LuceneManager.SERIALIZED_OBJECTS_LOCATION);
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(dir.getCanonicalPath() + "/" + simpleQAFile + ".ser"));
        return (SimpleQA) objectInputStream.readObject();
    }

    private List<QA> scoreLuceneResults(String question, List<QA> searchedResults) {
        List<QaScorer> qaScorers = new ArrayList<>();
        qaScorers.add(new AnswerFrequency(0.4)); //TODO CONFIG!!!!!
        qaScorers.add(new SimilarityToUserQuestion(0.5));
        qaScorers.add(new SimpleTimeDifference(0.1));
        for (QaScorer qaScorer : qaScorers) {
            qaScorer.score(question, searchedResults);
        }
        return searchedResults;
    }

    private QA getBestAnswer(String question, List<QA> scoredQas) {
        if (scoredQas.size() == 0 || scoredQas == null) {
            return new QA(question, this.configParser.getNoAnswerFoundMsg(), "", "", 0);
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