package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ApiOperations {
    /**
     * Centralized reusable operation annotations.
     */
    private ApiOperations() {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Retrieve a Document’s Table Of Contents (by Calendar Year)")
    public @interface ContentsOfCalendarYear {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @Operation(summary = "Retrieve a Document’s Table Of Contents (by Regnal Year)")
    public @interface ContentsOfRegnalYear {}
}
