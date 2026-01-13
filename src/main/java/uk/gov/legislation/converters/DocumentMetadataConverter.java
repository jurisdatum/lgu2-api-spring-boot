package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.CommonMetadata;
import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.api.responses.meta.AssociatedDocument;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.util.*;

import java.util.Collections;
import java.util.List;

public class DocumentMetadataConverter {

    public static DocumentMetadata convert(Metadata simple) {
        DocumentMetadata converted = new DocumentMetadata();
        convert(simple, converted);
        return converted;
    }

    public static void convert(Metadata simple, DocumentMetadata converted) {
        convertCommon(simple, converted);
        // perhaps this should be combined with fragment metadata
        converted.unappliedEffects = simple.rawEffects.stream()
            .sorted(EffectsComparator.INSTANCE)
            .map(EffectsFeedConverter::convertEffect).toList();
        if ("revised".equals(simple.status) && simple.version().equals(simple.versions().getLast())) {
            if (converted.pointInTime == null)
                UpToDate.setUpToDate(converted);
            else
                UpToDate.setUpToDate(converted, converted.pointInTime);
        }
        converted.altFormats = AlternateFormatConverter.convert(simple.alternatives);
    }

    static void convertCommon(Metadata simple, CommonMetadata converted) {
        converted.id = simple.id;
        converted.longType = simple.longType;
        converted.shortType = Types.longToShort(simple.longType);
        converted.year = simple.year;
        converted.regnalYear = simple.regnalYear;
        converted.number = simple.number;
        converted.altNumbers = convertAltNumbers(simple.altNums);
        converted.isbn = simple.isbn;
        converted.date = simple.date;
        if (simple.number != null)
            converted.cite = Cites.make(simple.longType, simple.year, simple.number, simple.altNums);
        else if (simple.isbn != null)
            converted.cite = "ISBN " + ISBN.format(simple.isbn);
        converted.version = simple.version();
        converted.status = simple.status;
        converted.title = simple.title;
        converted.extent = ExtentConverter.convert(simple.extent);
        converted.subjects = convertSubjects(simple);
        converted.lang = simple.lang;
        converted.publisher = simple.publisher;
        converted.modified = simple.modified;
        converted.versions = simple.versions();

        if (simple.hasParts != null) {
            converted.has.introduction = simple.hasParts.introduction != null;
            converted.has.signature = simple.hasParts.signature != null;
            converted.has.schedules = simple.hasParts.schedules != null;
            converted.has.note = simple.hasParts.note != null;
        }

        converted.schedules = simple.schedules;
        converted.formats = simple.formats();
        converted.pointInTime = simple.getPointInTime().orElse(null);
        converted.alternatives = AssociatedDocumentConverter.convert(simple.alternatives, AssociatedDocument.Type.Alternative);
        converted.associated = AssociatedDocumentConverter.convertAssociated(simple);
    }

    static List<CommonMetadata.AltNumber> convertAltNumbers(List<Metadata.AltNum> altNums) {
        if (altNums == null)
            return Collections.emptyList();
        return altNums.stream().map(DocumentMetadataConverter::convertAltNumber).toList();
    }

    protected static CommonMetadata.AltNumber convertAltNumber(Metadata.AltNum simple) {
        CommonMetadata.AltNumber converted = new CommonMetadata.AltNumber();
        converted.category = simple.category;
        converted.value = simple.value;
        return converted;
    }

    /* return null for non-secondary types */
    private static List<String> convertSubjects(Metadata simple) {
        Type type = Types.get(simple.longType);
        if (type == null)
            return null;
        if (!type.category().equals(Type.Category.Secondary))
            return null;
        if (simple.subjects == null)
            return List.of();
        return simple.subjects;
    }

}
