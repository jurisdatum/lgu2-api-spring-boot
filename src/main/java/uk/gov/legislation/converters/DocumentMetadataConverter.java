package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.CommonMetadata;
import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.util.Cites;
import uk.gov.legislation.util.EffectsComparator;
import uk.gov.legislation.util.Types;
import uk.gov.legislation.util.UpToDate;

import java.util.List;

public class DocumentMetadataConverter {

    public static DocumentMetadata convert(Metadata simple) {
        DocumentMetadata converted = new DocumentMetadata();
        convert(simple, converted);
        converted.unappliedEffects = simple.rawEffects.stream()
            .sorted(EffectsComparator.INSTANCE)
            .map(EffectsFeedConverter::convertEffect).toList();
        // maybe don't do this if version is not current
        UpToDate.setUpToDate(converted);
        return converted;
    }

    protected static void convert(Metadata simple, CommonMetadata converted) {
        converted.id = simple.id;
        converted.longType = simple.longType;
        converted.shortType = Types.longToShort(simple.longType);
        converted.year = simple.year;
        converted.regnalYear = simple.regnalYear;
        converted.number = simple.number;
        converted.altNumbers = convertAltNumbers(simple.altNums);
        converted.date = simple.date;
        converted.cite = Cites.make(simple.longType, simple.year, simple.number, simple.altNums);
        converted.version = simple.version();
        converted.status = simple.status;
        converted.title = simple.title;
        converted.extent = ExtentConverter.convert(simple.extent);
        converted.lang = simple.lang;
        converted.publisher = simple.publisher;
        converted.modified = simple.modified;
        converted.versions = simple.versions();
        converted.schedules = simple.schedules;
        converted.formats = simple.formats();
    }

    protected static List<CommonMetadata.AltNumber> convertAltNumbers(List<Metadata.AltNum> altNums) {
        if (altNums == null)
            return null;
        return altNums.stream().map(DocumentMetadataConverter::convertAltNumber).toList();
    }

    protected static CommonMetadata.AltNumber convertAltNumber(Metadata.AltNum simple) {
        CommonMetadata.AltNumber converted = new CommonMetadata.AltNumber();
        converted.category = simple.category;
        converted.value = simple.value;
        return converted;
    }

}
