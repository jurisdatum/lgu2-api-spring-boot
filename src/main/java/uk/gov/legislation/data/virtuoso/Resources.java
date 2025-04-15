package uk.gov.legislation.data.virtuoso;

import uk.gov.legislation.data.virtuoso.rdf.TypedValue;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Resources {

    public static class RDF {

        public static final String Prefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        public static final String Type = Prefix + "type";

    }

    public static class Leg {

        public static final String Prefix = "http://www.legislation.gov.uk/def/legislation/";

        public static final String Item = Prefix + "Item";
        public static final String Interpretation = Prefix + "Interpretation";
        public static final String Legislation = Prefix + "Legislation";
        public static final String OriginalInterpretation = Prefix + "OriginalInterpretation";
        public static final String CurrentInterpretation = Prefix + "CurrentInterpretation";

        /* document types */

        public static final String UKPGA = Prefix + "UnitedKingdomPublicGeneralAct";
        public static final Set<String> DocumentTypes = Set.of(
            UKPGA
        );

        /* properties */

        public static final String Year = Prefix + "year";
        public static final String Number = Prefix + "number";
        public static final String Title = Prefix + "title";
        public static final String Citation = Prefix + "citation";
        public static final String FullCitation = Prefix + "fullCitation";
        public static final String CommentaryCitation = Prefix + "commentaryCitation";
        public static final String OriginalLanguage = Prefix + "originalLanguageOfTextIsoCode";
        public static final String Language = Prefix + "languageOfTextIsoCode";
        public static final String ShortTitle = Prefix + "shortTitle";
        public static final String LongTitle = Prefix + "longTitle";

    }

    private static boolean is(String uri, Map<URI, List<TypedValue>> properties) {
        URI prop = URI.create(RDF.Type);
        if (!properties.containsKey(prop))
            return false;
        List<TypedValue> values = properties.get(prop);
        return values.stream().anyMatch(v -> v.value().equals(uri));
    }

    public static boolean isItem(Map<URI, List<TypedValue>> properties) {
        return is(Leg.Item, properties);
    }

    public static boolean isInterpretation(Map<URI, List<TypedValue>> properties) {
        return is(Leg.Interpretation, properties);
    }

}
