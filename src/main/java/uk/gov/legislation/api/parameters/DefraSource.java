package uk.gov.legislation.api.parameters;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "source",
    description = "The source of legislation",
    schema = @Schema(
        examples = { "domestic", "eu", "hybrid", "international" }
    )
)
public @interface DefraSource { }
