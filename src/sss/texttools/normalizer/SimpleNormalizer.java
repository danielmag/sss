package sss.texttools.normalizer;

import l2f.nlp.NormalizerSimple;

public class SimpleNormalizer extends Normalizer {

    @Override
    public String normalize(String text) {
        return NormalizerSimple.normPunctLCaseDMarks(text);
    }
}
