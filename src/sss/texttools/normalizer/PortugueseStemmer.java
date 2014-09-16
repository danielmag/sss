package sss.texttools.normalizer;

import org.apache.commons.lang3.StringUtils;
import ptstemmer.Stemmer;
import ptstemmer.exceptions.PTStemmerException;
import ptstemmer.implementations.OrengoStemmer;
import ptstemmer.support.PTStemmerUtilities;

public class PortugueseStemmer extends Normalizer {
    Stemmer stemmer;

    public PortugueseStemmer() throws PTStemmerException {
        stemmer = new OrengoStemmer();
        stemmer.enableCaching(1000);   //Optional
        stemmer.ignore(PTStemmerUtilities.fileToSet("./resources/namedEntities/namedEntities.txt"));  //Optional
    }

    @Override
    public String normalize(String text) {
            return StringUtils.join(stemmer.getPhraseStems(text), " ");
    }
}
