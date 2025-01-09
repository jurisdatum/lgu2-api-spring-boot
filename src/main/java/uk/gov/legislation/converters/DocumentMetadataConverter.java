package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.endpoints.document.service.EffectsConverter;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.Types;

public class DocumentMetadataConverter {

    public static DocumentMetadata convert(Metadata simple) {
        DocumentMetadata converted = new DocumentMetadata();
        convert(simple, converted);
        return converted;
    }

    protected static void convert(Metadata simple, DocumentMetadata converted) {
        converted.id = simple.id;
        converted.longType = simple.longType;
        converted.shortType = Types.longToShort(simple.longType);
        converted.year = simple.year;
        converted.regnalYear = simple.regnalYear;
        converted. number = simple.number;
        converted.altNumbers = simple.altNums;
        converted.date = simple.date;
        converted.cite = Cites.make(simple.longType, simple.year, simple.number, simple.altNums);
        converted.version = simple.version();
        converted.status = simple.status;
        converted.title = simple.title;
        converted.extent = simple.extent();
        converted.lang = simple.lang;
        converted.publisher = simple.publisher;
        converted.modified = simple.modified;
        converted.versions = simple.versions();
        converted.schedules = simple.schedules();
        converted.formats = simple.formats();
        converted.unappliedEffects = EffectsConverter.convert(simple.rawEffects());
    }

}
