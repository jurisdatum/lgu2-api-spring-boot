package uk.gov.legislation.api.responses;

import io.swagger.v3.oas.annotations.media.Schema;

public class Document extends AnyDocument<DocumentMetadata> {

    @Schema
    public String html;

    public Document(DocumentMetadata meta, String html) {
        this.meta = meta;
        this.html = html;
    }

}
