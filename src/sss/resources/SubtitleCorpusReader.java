package sss.resources;

import com.db4o.ObjectContainer;
import org.apache.lucene.index.IndexWriter;
import sss.dialog.SimpleQA;
import sss.texttools.normalizer.Normalizer;

import java.io.*;
import java.text.NumberFormat;
import java.util.List;

public class SubtitleCorpusReader extends CorpusReader {


    @Override
    public void read(IndexWriter writer, ObjectContainer db, File[] files, List<Normalizer> normalizers) throws IOException {
        for (File file : files) {
            System.out.println("Creating lucene indexes and database for " + file.getName() + "...");
            BufferedReader reader = new BufferedReader(new FileReader(file.getCanonicalPath()));
            String line;
            String subId;
            String question;
            String answer;
            long uniqueIdentifier = 0;
            int previousDialogId = 0;
            long totalLines = count(file.getCanonicalPath());
            System.out.println(totalLines);
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
                temp = reader.readLine();
                assert (temp.startsWith("DialogId"));
                int dialogId = Integer.parseInt(getSubstringAfterHyphen(temp));

                temp = reader.readLine();
                assert (temp.startsWith("Diff"));
                long diff = Long.parseLong(getSubstringAfterHyphen(temp));

                temp = reader.readLine();
                assert (temp.startsWith("I"));
                question = getSubstringAfterHyphen(temp); //assumes the corpus does not have empty questions

                temp = reader.readLine();
                assert (temp.startsWith("R"));
                answer = getSubstringAfterHyphen(temp); //assumes the corpus does not have empty answers
                answer = answer.trim();

                String normalizedAnswer = Normalizer.applyNormalizations(answer, normalizers);
                String normalizedQuestion = Normalizer.applyNormalizations(question, normalizers);

                SimpleQA simpleQA;
                if (dialogId == previousDialogId + 1) {
                    simpleQA = new SimpleQA(uniqueIdentifier, uniqueIdentifier - 1, question, answer, normalizedQuestion, normalizedAnswer, diff);
                } else {
                    simpleQA = new SimpleQA(uniqueIdentifier, -1, question, answer, normalizedQuestion, normalizedAnswer, diff);
                }
                db.store(simpleQA);
                uniqueIdentifier++;
                previousDialogId = dialogId;
                addDoc(writer, normalizedQuestion, String.valueOf(uniqueIdentifier));
            }
            System.out.println(lineNum);
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
