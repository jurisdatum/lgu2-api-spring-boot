package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.Associated;
import uk.gov.legislation.api.responses.meta.AssocMeta;
import uk.gov.legislation.api.responses.meta.MetaCore;

import uk.gov.legislation.data.marklogic.notes.EN;
import uk.gov.legislation.util.Links;

public class ExplanatoryNotesConvertor {

        public static Associated convert (EN en) {
            if(en == null) {
                return null;
            }
            Associated associated = new Associated();
            associated.meta = toAssocMeta(en.metadata);
            return associated;
        }

        private static AssocMeta toAssocMeta(EN.Metadata metadata) {
            if(metadata == null) {
                return null;
            }
            AssocMeta meta = new AssocMeta();
            meta.id = Links.shorten(metadata.identifier);
            meta.altFormats= metadata.alternatives.stream().map(AlternateFormatConverter::convert).toList();
            meta.associatedWith = toMetaCore(metadata.enMetadata);
            return meta;
        }

        private static MetaCore toMetaCore(EN.ENMetadata enMetadata) {
            if(enMetadata == null) {
                return null;
            }
            MetaCore core = new MetaCore();

            core.longType = enMetadata.documentClassification.documentMainType.value;
            core.year = enMetadata.year.value;
            core.number = enMetadata.number.value;

            return core;
        }
}

