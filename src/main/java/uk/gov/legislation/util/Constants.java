package uk.gov.legislation.util;

import lombok.Getter;

@Getter
public enum Constants {


    // Transformation failure messages
    TRANSFORMATION_FAIL_AKN("Failed to transform CLML to AKN"),
    TRANSFORMATION_FAIL_HTML("Failed to transform CLML to HTML"),
    TRANSFORMATION_FAIL_JSON("Failed to transform CLML to JSON"),

    // Document not found messages
    DOCUMENT_NOT_FOUND("Document not found: %s, %s, %d");
     final String error;

    Constants(String error) {
        this.error = error;
    }
}


