package uk.gov.legislation.api;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Reusable Swagger tags for controllers.
public class Tags {

    private Tags() {}

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Tag(
        name = "Tables of contents")
    public @interface TablesOfContents {}
}
