package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "chapter",
    description = "The sequential number of an Act (except an Act of the Scottish Parliament) is called a ‘Chapter number",
    example = "Police Reform Act 2002 (c. 30)"
)
public @interface Chapter {}
