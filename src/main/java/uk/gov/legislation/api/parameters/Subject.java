package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "subject",
    description = "Filter By Specific Subject (e.g. 'ACQUISITION OF LAND', 'BANKRUPTCY', 'CHARITIES').",
    example = "ACQUISITION OF LAND"
)
public @interface Subject {}
