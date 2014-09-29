package sss.resources;

import com.db4o.ObjectContainer;
import org.apache.lucene.index.IndexWriter;
import sss.dialog.SimpleL2RQA;
import sss.dialog.SimpleQA;
import sss.main.Main;
import sss.texttools.normalizer.Normalizer;

import java.io.*;
import java.text.NumberFormat;
import java.util.List;

public class SubtitleCorpusL2RReader extends CorpusReader {


    @Override
    public void read(IndexWriter writer, ObjectContainer db, File[] files, List<Normalizer> normalizers) throws IOException {
        for (File file : files) {
            System.out.println("Creating lucene indexes and database for " + file.getName() + "...");
            BufferedReader reader = new BufferedReader(new FileReader(file.getCanonicalPath()));
            String line;
            String subId;
            String question;
            String answer;
            long internalId = -1;
            int previousDialogId = 0;
            long totalLines = count(file.getCanonicalPath());
            long step = totalLines / 1000;
            long lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                if ((lineNum % step) == 0) {
                    System.out.println(getPercentage(lineNum, totalLines));
                }
                if (line.trim().length() == 0) {
                    continue;
                }
                String temp = line;
                assert (temp.startsWith("SubId"));
                subId = getSubstringAfterHyphen(temp);
                temp = reader.readLine(); lineNum++;
                assert (temp.startsWith("DialogId"));
                int dialogId = Integer.parseInt(getSubstringAfterHyphen(temp));

                temp = reader.readLine(); lineNum++;
                assert (temp.startsWith("Diff"));
                long diff = Long.parseLong(getSubstringAfterHyphen(temp));

                temp = reader.readLine(); lineNum++;
                assert (temp.startsWith("I"));
                question = getSubstringAfterHyphen(temp); //assumes the corpus does not have empty questions

                temp = reader.readLine(); lineNum++;
                assert (temp.startsWith("R"));
                answer = getSubstringAfterHyphen(temp); //assumes the corpus does not have empty answers
                answer = answer.trim();

                String normalizedAnswer = Normalizer.applyNormalizations(answer, normalizers);
                String normalizedQuestion = Normalizer.applyNormalizations(question, normalizers);

                String normalizedAnswerHeadWord = normalizedAnswer.split("\\s+")[0];
                String normalizedQuestionHeadWord = normalizedQuestion.split("\\s+")[0];

                int answerHeadWordIndex = -1;
                for (int i = 0; i < Main.answerHeadWords.length; i++) {
                    String w = Main.answerHeadWords[i];
                    if (w.equalsIgnoreCase(normalizedAnswerHeadWord)) {
                        answerHeadWordIndex = i;
                        break;
                    }
                }
                int questionHeadWordIndex = -1;
                for (int i = 0; i < Main.questionHeadWords.length; i++) {
                    String w = Main.questionHeadWords[i];
                    if (w.equalsIgnoreCase(normalizedQuestionHeadWord)) {
                        questionHeadWordIndex = i;
                        break;
                    }
                }

                SimpleL2RQA simpleL2RQA;
                if (dialogId == previousDialogId + 1) {
                    simpleL2RQA = new SimpleL2RQA(internalId, questionHeadWordIndex, answerHeadWordIndex, question, answer, normalizedQuestion, normalizedAnswer, diff);
                } else {
                    simpleL2RQA = new SimpleL2RQA(-1, questionHeadWordIndex, answerHeadWordIndex, question, answer, normalizedQuestion, normalizedAnswer, diff);
                }
                db.store(simpleL2RQA);
                internalId = db.ext().getID(simpleL2RQA);
                previousDialogId = dialogId;
                addDoc(writer, normalizedQuestion, String.valueOf(internalId));
            }
            System.out.println();
        }
    }

    private String getPercentage(long partial, long total) {
        NumberFormat defaultFormat = NumberFormat.getPercentInstance();
        defaultFormat.setMinimumFractionDigits(1);
        return defaultFormat.format(partial / (double) total);
    }

    private String getSubstringAfterHyphen(String temp) {
        return temp.substring(temp.indexOf('-') + 2, temp.length());
    }

    private int count(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(filename));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if(endsWithoutNewLine) {
                ++count;
            }
            return count;
        } finally {
            is.close();
        }
    }
}
