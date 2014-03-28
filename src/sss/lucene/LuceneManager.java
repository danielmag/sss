package sss.lucene;

import l2f.nlp.NormalizerSimple;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import sss.dialog.QA;
import sss.dialog.SimpleQA;
import sss.dialog.WholeDialog;
import sss.dialog.evaluator.AnswerFrequency;
import sss.dialog.evaluator.QaScorer;
import sss.dialog.evaluator.SimilarityToUserQuestion;
import sss.dialog.evaluator.SimpleTimeDifference;
import sss.resources.ConfigParser;

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

    private List<QA> loadLuceneResults(List<Document> docList) throws IOException, ClassNotFoundException {
        List<QA> qas = new ArrayList<>();
        for (Document d : docList) {
            String question = d.get("question");
            String[] answerStrings = d.get("answer").split(LuceneAlgorithm.DELIMITER);
            String answer = answerStrings[0];
            String wholeDialogFile = answerStrings[1];
            int dialogId = Integer.parseInt(answerStrings[2]);

            WholeDialog wholeDialog = deserializeWholeDialog(wholeDialogFile);
            SimpleQA simpleQA = wholeDialog.getSimpleQA(dialogId);
            QA qa = new QA(question, answer, simpleQA.getDiff());
            qas.add(qa);
        }
        return qas;
    }

    private WholeDialog deserializeWholeDialog(String wholeDialogFile) throws IOException, ClassNotFoundException {
        File dir = new File(LuceneManager.SERIALIZED_OBJECTS_LOCATION);
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(dir.getCanonicalPath() + "/" + wholeDialogFile + ".ser"));
        return (WholeDialog) objectInputStream.readObject();
    }

    public String getAnswer(String question) throws IOException, ParseException, ClassNotFoundException {
        String normQuestion = NormalizerSimple.normPunctLCaseDMarks(question);
        List<Document> luceneDocs = this.luceneAlgorithm.search(normQuestion, this.configParser.getHitsPerQuery());
        List<QA> searchedResults = loadLuceneResults(luceneDocs);
        List<QA> scoredQas = scoreLuceneResults(normQuestion, searchedResults);
        QA answer = getBestAnswer(question, scoredQas);
        addGivenAnswer(answer);
        return answer.getAnswer();
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
            return new QA(question, this.configParser.getNoAnswerFoundMsg(), 0);
        }
        double max = 0;
        QA bestQa = null;
        for (QA qa : scoredQas) {
            System.out.println("Q - " + qa.getQuestion());
            System.out.println("A - " + qa.getAnswer());
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