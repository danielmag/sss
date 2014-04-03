package sss.texttools.normalizer;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;
import sss.lucene.LuceneManager;
import sss.texttools.TextAnalyzer;

import java.util.List;

public class EnglishLemmatizer extends Normalizer {
    TextAnalyzer textAnalyzer;

    public EnglishLemmatizer() {
        this.textAnalyzer = new TextAnalyzer(LuceneManager.ANALYZER_PROPERTIES);
    }

    private String getLemmatizedString(List<CoreMap> sentences) {
        StringBuilder lemmatizedAnswer = new StringBuilder();
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
                if (!lemma.matches(".*\\p{L}.*")) { //\p{L} = any letter
                    continue;
                }
                lemmatizedAnswer.append(lemma + " ");
            }
        }
        return lemmatizedAnswer.toString();
    }

    public String normalize(String text) {
        return getLemmatizedString(this.textAnalyzer.analyze(text));
    }
}
