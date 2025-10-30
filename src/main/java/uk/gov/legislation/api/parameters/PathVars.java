package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class PathVars {
    /**
     * Centralized reusable PathVariable parameter documentation for API.
     * Use with @PathVariable on controller methods.
     */
    private PathVars() {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The name of a monarch, relative to which the year is given", example = "Eliz2")
    public @interface Monarch { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The number of the document", example = "1")
    public @interface Number { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The page number for paged results", example = "1")
    public @interface Page { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The internal id of the part, section, etc.", example = "section-2")
    public @interface Section { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The sort order", example = "title")
    public @interface Sort { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The year of the document", example = "2025")
    public @interface Year { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The years of the document", example = "10-11")
    public @interface Years { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The title of the document", example = "Finance Act")
    public @interface Title { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The type of the document", example = "ukpga")
    public @interface Type { }
}
