package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.Associated;
import uk.gov.legislation.api.responses.meta.AssocMeta;
import uk.gov.legislation.api.responses.meta.MetaCore;
import uk.gov.legislation.data.marklogic.impacts.ImpactAssessment;
import uk.gov.legislation.util.Links;

import static uk.gov.legislation.util.Types.longToShort;

public class ImpactAssessmentConverter {

    public static Associated convert(ImpactAssessment clml) {
        Associated two = new Associated();
        two.meta = convert(clml.metadata);
        return two;
    }

    public static AssocMeta convert(ImpactAssessment.Metadata clml) {
        AssocMeta meta = new AssocMeta();
        meta.id = Links.shorten(clml.identifier);
        meta.longType = clml.impactAssessmentMetadata.documentClassification.documentMainType.value;
        meta.shortType = "ukia";
        meta.year = clml.impactAssessmentMetadata.year.value;
        meta.number = clml.impactAssessmentMetadata.number.value;
        meta.associatedWith = convert(clml.legislation);
        meta.stage = clml.impactAssessmentMetadata.documentClassification.documentStage.value;
        meta.department = clml.impactAssessmentMetadata.department.value;
        meta.altFormats = AlternateFormatConverter.convert(clml.alternatives);
        return meta;
    }

    public static MetaCore convert(ImpactAssessment.Legislation leg) {
        if (leg == null)
            return null;
        MetaCore aw = new MetaCore();
        aw.id = Links.shorten(leg.uri.toString());
        aw.longType = leg.clazz;
        aw.shortType = longToShort(leg.clazz);
        aw.year = leg.year;
        aw.number = leg.number;
        return aw;
    }

}
