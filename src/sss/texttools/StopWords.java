package sss.texttools;

import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopWords {
    CharArraySet stopWords;

    public StopWords(String stopWordsLocation) throws IOException {
        this.stopWords = this.getStopWords(stopWordsLocation);
    }

    private CharArraySet getStopWords(String stopWordsLocation) throws IOException {
        Pattern pattern = Pattern.compile("^[\\p{L}]+");
        List<String> stopWords = new ArrayList<>();
        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(stopWordsLocation)), Charset.defaultCharset()));
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                stopWords.add(matcher.group());
            }
        }
        return StopFilter.makeStopSet(Version.LUCENE_43, stopWords, true);
    }

    public CharArraySet getStopWords() {
        return stopWords;
    }
}
