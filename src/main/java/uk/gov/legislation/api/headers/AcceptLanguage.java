package uk.gov.legislation.api.headers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "Accept-Language",
    description = "Language of the document",
    in = ParameterIn.HEADER,
    schema = @Schema(
        type = "string",
        allowableValues = { "en", "cy" },
        example = "en"
    )
)
public @interface AcceptLanguage { }
