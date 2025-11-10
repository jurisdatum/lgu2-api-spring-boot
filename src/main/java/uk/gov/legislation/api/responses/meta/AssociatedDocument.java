package uk.gov.legislation.api.responses.meta;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.LocalDate;

/**
 * Represents a document associated with a piece of legislation, such as explanatory notes,
 * impact assessments, correction slips, alternative formats, or other supplementary documents.
 * Provides a unified API response format for all types of related documents.
 */
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

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String label;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(
        description = "Indicates the stage of the development of the legislation to which the Impact Assessment relates, which may be before its development (Consultation), during its development (Development), after the drafting of the document (Final), after its amendment in Parliament (Enactment) or after its enactment (Post-Implementation)",
        allowableValues = { "Consultation", "Development", "Final", "Enactment", "Post-Implementation" }
    )
    public String stage;

    public AssociatedDocument(Type type, URI uri) {
        this.type = type;
        this.uri = uri;
    }

}
