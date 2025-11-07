package uk.gov.legislation.api.parameters;


import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "source",
    description = "Filter by the source of the document, such as the publishing authority.",
    example = "legislation.gov.uk"
)
public @interface Source {}
