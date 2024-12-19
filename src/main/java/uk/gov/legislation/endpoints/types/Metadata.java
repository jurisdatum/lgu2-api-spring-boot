package uk.gov.legislation.endpoints.types;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public class Metadata {

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:identifier", example = "http://www.legislation.gov.uk/ukpga/2024/1")
    private String identifier;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:title", example = "Post Office (Horizon System) Compensation Act 2024")
    private String title;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:publisher", example = "Statute Law Database")
    private String publisher;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:modified", example = "2024-04-18")
    private LocalDate modified;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:contributor", example = "Expert Participation")
    private String contributor;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/terms/")
    @Schema(name = "dct:valid")
    private LocalDate valid;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @Schema(name = "ukm:PrimaryMetadata")
    private PrimaryMetadata primaryMetadata;

    public static class PrimaryMetadata {

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:DocumentClassification")
        private DocumentClassification documentClassification;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:Year")
        private Year year;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:Number")
        private Number number;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:AlterativeNumber")
        private List<AlternativeNumber> alterativeNumber;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:EnactmentDate")
        private EnactmentDate enactmentDate;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:ISBN")
        private ISBN iSBN;

    }

    public static class DocumentClassification {

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name="ukm:DocumentCategory")
        private DocumentCategory documentCategory;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name="ukm:DocumentMainType")
        private DocumentMainType documentMainType;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name="ukm:DocumentStatus")
        private DocumentStatus documentStatus;

    }

    public static class DocumentCategory {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(allowableValues = { "primary" })
        private String value;

    }

    public static class DocumentMainType {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(allowableValues = { "UnitedKingdomPublicGeneralAct" })
        private String value;

    }

    public static class DocumentStatus {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(allowableValues = { "final", "revised" }) // ToDo "draft"
        private String value;

    }

    public static class Year {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "2024")
        private int value;

    }

    public static class Number {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "1")
        private int value;

    }

    public static class AlternativeNumber {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "W")
        private String category;

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "1")
        private String value;

    }

    public static class EnactmentDate {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "2024-01-25")
        private LocalDate date;

    }

    public static class ISBN {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "9780105702443")
        private String value;

    }

}
