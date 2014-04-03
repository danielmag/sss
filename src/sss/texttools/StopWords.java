package sss.texttools;

import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import sss.texttools.normalizer.Normalizer;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopWords {
    private CharArraySet stopWords;

    public StopWords(String stopWordsLocation, List<Normalizer> normalizers) throws IOException {
        this.stopWords = this.getStopWords(stopWordsLocation, normalizers);
    }

    private CharArraySet getStopWords(String stopWordsLocation, List<Normalizer> normalizers) throws IOException {
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
                stopWords.add(Normalizer.applyNormalizations(matcher.group(), normalizers));
            }
        }
        return StopFilter.makeStopSet(Version.LUCENE_43, stopWords, true);
    }

    public CharArraySet getStopWords() {
        return stopWords;
    }

    public List<String> getStringListWithoutStopWords(List<String> tokenizedQuestion) {
        List<String> stringList = new ArrayList<>();
        for (String s : tokenizedQuestion) {
            if (!this.stopWords.contains(s)) {
                stringList.add(s);
            }
        }
        return stringList;
    }
}
