package uk.gov.legislation.api.responses.meta;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.LocalDate;

public class AssociatedDocument {

    /** See schemaLegislationMetadata.xsd */
    @SuppressWarnings("java:S115")
    public enum Type {
        Note,
        PolicyEqualityStatement,
        Alternative,
        CorrectionSlip,
        CodeOfPractice,
        CodeOfConduct,
        TableOfOrigins,
        TableOfDestinations,
        OrderInCouncil,
        ImpactAssessment,
        Other,
        ExplanatoryDocument,
        TranspositionNote,
        UKRPCOpinion;
    }

    @JsonProperty
    public final Type type;

    @JsonProperty
    public final URI uri;

    @JsonProperty
    public String name;

    @JsonProperty
    public LocalDate date;

    @JsonProperty
    public Integer size;

    public AssociatedDocument(Type type, URI uri) {
        this.type = type;
        this.uri = uri;
    }

}
