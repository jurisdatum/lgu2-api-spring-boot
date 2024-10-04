package uk.gov.legislation.api.types;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

public class Metadata {

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:identifier", example = "http://www.legislation.gov.uk/ukpga/2024/1")
    public String identifier;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:title", example = "Post Office (Horizon System) Compensation Act 2024")
    public String title;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:publisher", example = "Statute Law Database")
    public String publisher;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:modified", example = "2024-04-18")
    public LocalDate modified;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/elements/1.1/")
    @Schema(name = "dc:contributor", example = "Expert Participation")
    public String contributor;

    @JacksonXmlProperty(namespace = "http://purl.org/dc/terms/")
    @Schema(name = "dct:valid")
    public LocalDate valid;

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @Schema(name = "ukm:PrimaryMetadata")
    public PrimaryMetadata PrimaryMetadata;

    public static class PrimaryMetadata {

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:DocumentClassification")
        public DocumentClassification DocumentClassification;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:Year")
        public Year Year;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:Number")
        public Number Number;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:AlterativeNumber")
        public List<AlternativeNumber> AlterativeNumber;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:EnactmentDate")
        public EnactmentDate EnactmentDate;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name = "ukm:ISBN")
        public ISBN ISBN;

    }

    public static class DocumentClassification {

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name="ukm:DocumentCategory")
        public DocumentCategory DocumentCategory;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name="ukm:DocumentMainType")
        public DocumentMainType DocumentMainType;

        @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
        @Schema(name="ukm:DocumentStatus")
        public DocumentStatus DocumentStatus;

    }

    public static class DocumentCategory {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(allowableValues = { "primary" })
        public String Value;

    }

    public static class DocumentMainType {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(allowableValues = { "UnitedKingdomPublicGeneralAct" })
        public String Value;

    }

    public static class DocumentStatus {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(allowableValues = { "final", "revised" })
        public String Value;

    }

    public static class Year {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "2024")
        public int Value;

    }

    public static class Number {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "1")
        public int Value;

    }

    public static class AlternativeNumber {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "W")
        public String Category;

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "1")
        public String Value;

    }

    public static class EnactmentDate {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "2024-01-25")
        public LocalDate Date;

    }

    public static class ISBN {

        @JacksonXmlProperty(isAttribute = true)
        @XmlAttribute
        @Schema(example = "9780105702443")
        public String Value;

    }

}
