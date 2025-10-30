package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ApiParams {
    /**
     * Centralized reusable query parameter documentation for the API.
     * Use with @RequestParam on controller methods.
     */
    private ApiParams() {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @Parameter(description = "The version of the document", example = "enacted")
    public @interface Version { }
}
