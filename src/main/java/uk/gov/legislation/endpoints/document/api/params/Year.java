package uk.gov.legislation.endpoints.document.api.params;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Parameter(description = "A four digit calendar year", example = "2024")
public @interface Year { }