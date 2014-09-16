package sss.texttools.normalizer;

import ptstemmer.exceptions.PTStemmerException;
import sss.dialog.evaluator.*;

import java.util.ArrayList;
import java.util.List;

public class NormalizerFactory {

    public List<Normalizer> createNormalizers(List<String> normalizersStrings) throws PTStemmerException {
        List<Normalizer> normalizers = new ArrayList<>();
        for (String s : normalizersStrings) {
            String[] strings = s.split(",");
            switch(strings[0]) {
                case "RemoveDiacriticalMarks" : normalizers.add(new SimpleNormalizer()); break;
                case "EnglishLemmatizer" : normalizers.add(new EnglishLemmatizer()); break;
                case "PortugueseStemmer" : normalizers.add(new PortugueseStemmer()); break;
                default: throw new RuntimeException("You have inserted a Normalization that does not exist");
            }
        }

        return normalizers;
    }
}
