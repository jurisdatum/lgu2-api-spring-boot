package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

public class Document {

    @Schema
    public DocumentMetadata meta;

    @Schema
    public String html;

    public Document(DocumentMetadata meta, String html) {
        this.meta = meta;
        this.html = html;
    }

}
