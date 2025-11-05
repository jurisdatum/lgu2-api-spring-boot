package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.ExtendedMetadata;
import uk.gov.legislation.api.responses.meta.Provision;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.TitledThing;
import uk.gov.legislation.util.Links;

import java.util.List;

public class ExtendedMetadataConverter {

    public static ExtendedMetadata convert(Metadata simple) {
        ExtendedMetadata converted = new ExtendedMetadata();
        DocumentMetadataConverter.convert(simple, converted);
        converted.confersPower = convert(simple.confersPower);
        converted.blanketAmendments = convert(simple.blanketAmendments);
        return converted;
    }

    static List<Provision> convert(List<TitledThing> things) {
        if (things == null)
            return List.of();
        return things.stream().map(ExtendedMetadataConverter::convert1).toList();
    }

    static Provision convert1(TitledThing thing) {
        Provision provision = new Provision();
        if (thing.uri != null) {
            Links.Components components = Links.parse(thing.uri.toASCIIString());
            provision.href = components.fragment().orElse(null);
            if (provision.href != null)
                provision.id = provision.href.replace('/', '-');
        }
        if (thing.title != null) {
            provision.title = thing.title;
            int i = thing.title.indexOf(':');
            provision.label = i >= 0 ? thing.title.substring(0, i) : thing.title;
        }
        return provision;
    }

}
