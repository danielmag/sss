package sss.texttools.normalizer;

import java.util.List;

public abstract class Normalizer { //TODO treetagger
    public abstract String normalize(String text);

    public static String applyNormalizations(String s, List<Normalizer> normalizers) {
        String normalized = new String(s);
        for (Normalizer normalizer : normalizers) {
            normalized = normalizer.normalize(normalized);
        }
        return normalized;
    }
}
