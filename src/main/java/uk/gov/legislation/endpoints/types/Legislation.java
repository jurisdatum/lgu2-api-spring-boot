package uk.gov.legislation.endpoints.types;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@JacksonXmlRootElement(localName = "Legislation", namespace = "http://www.legislation.gov.uk/namespaces/legislation")
@Schema(name = "Legislation", externalDocs = @ExternalDocumentation(description = "XML Schema Definition", url = "http://www.legislation.gov.uk/schema/legislation.xsd"))
public class Legislation {

    @JacksonXmlProperty(namespace = "http://www.legislation.gov.uk/namespaces/metadata")
    @Schema(name = "ukm:Metadata")
    public Metadata Metadata;

}
