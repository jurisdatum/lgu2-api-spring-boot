package uk.gov.legislation.converters.ld;

import uk.gov.legislation.api.responses.ld.Item;
import uk.gov.legislation.data.virtuoso.Resources;
import uk.gov.legislation.data.virtuoso.jsonld.ItemLD;
import uk.gov.legislation.data.virtuoso.jsonld.ValueAndLanguage;

public class ItemConverter {

    public static Item convert(ItemLD ld) {
        Item item = new Item();
        item.uri = ld.id;
        item.type = ld.type.stream()
            .filter(type -> !Resources.Leg.Item.equals(type))
            .filter(type -> !Resources.Leg.Legislation.equals(type))
            .map(type -> type.substring(Resources.Leg.Prefix.length()))
            .findFirst()
            .orElse(null);
        item.year = ld.year;
        item.session = ld.session == null ? null : ld.session.toString().substring(41);
        item.number = ld.number;
        item.title = ValueAndLanguage.get(ld.title, "en");
        item.welshTitle = ValueAndLanguage.get(ld.title, "cy");
        item.citation = ValueAndLanguage.get(ld.citation, "en");
        item.welshCitation = ValueAndLanguage.get(ld.citation, "cy");
        item.fullCitation = ValueAndLanguage.get(ld.fullCitation, "en");
        item.welshFullCitation = ValueAndLanguage.get(ld.fullCitation, "cy");
        item.commentaryCitation = ValueAndLanguage.get(ld.commentaryCitation, "en");
        item.welshCommentaryCitation = ValueAndLanguage.get(ld.commentaryCitation, "cy");
        item.originalLanguages = ld.originalLanguageOfTextIsoCode.stream()
            .map(value -> value.value).toList();
        item.interpretations = ld.interpretation;
        return item;
    }

}
