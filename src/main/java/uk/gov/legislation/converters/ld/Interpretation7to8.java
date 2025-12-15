package uk.gov.legislation.converters.ld;

import uk.gov.legislation.data.virtuoso.jsonld.Interpretation7;
import uk.gov.legislation.data.virtuoso.jsonld.Interpretation8;
import uk.gov.legislation.data.virtuoso.jsonld.Item;

public class Interpretation7to8 {

    public static Interpretation8 convert(Interpretation7 seven, Item item) {
        Interpretation8 eight = new Interpretation8();
        eight.uri = seven.id;
        eight.type = seven.type;
        eight.interpretationOf = item;
        eight.languageOfText = seven.languageOfText;
        eight.languageOfTextIsoCode = seven.languageOfTextIsoCode;
        eight.longTitle = seven.longTitle;
        eight.shortTitle = seven.shortTitle;
        eight.orderTitle = seven.orderTitle;
        eight.statuteTitle = seven.statuteTitle;
        eight.statuteTitleAbbreviated = seven.statuteTitleAbbreviated;
        eight.alternativeStatuteTitle = seven.alternativeStatuteTitle;
        eight.europeanUnionTitle = seven.europeanUnionTitle;
        eight.subjectDescription = seven.subjectDescription;
        eight.within = seven.within;
        eight.contains = seven.contains;
        return eight;
    }

}
