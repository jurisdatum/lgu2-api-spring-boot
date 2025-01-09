package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.FragmentMetadata;
import uk.gov.legislation.transform.simple.Metadata;

public class FragmentMetadataConverter {

    public static FragmentMetadata convert(Metadata simple) {
        FragmentMetadata converted = new FragmentMetadata();
        DocumentMetadataConverter.convert(simple, converted);
        converted.fragment = simple.fragment();
        converted.prev = simple.prev();
        converted.next = simple.next();
        converted.ancestors = simple.ancestors();
        converted.descendants = simple.descendants();
        return converted;
    }

}
