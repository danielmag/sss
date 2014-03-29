package sss.texttools;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import sss.lucene.LuceneManager;

import java.util.List;

public class Lemmatizer {

    public String getLemmatizedString(List<CoreMap> sentences) {
        String lemmatizedAnswer = "";
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (!lemma.matches("[\\p{L}]+")) {
                    continue;
                }
                lemmatizedAnswer += lemma + " ";
            }
        }
        return lemmatizedAnswer;
    }

    public String getLemmatizedString(String text) {
        TextAnalyzer textAnalyzer = new TextAnalyzer(LuceneManager.ANALYZER_PROPERTIES);
        return getLemmatizedString(textAnalyzer.analyze(text));
    }
}
