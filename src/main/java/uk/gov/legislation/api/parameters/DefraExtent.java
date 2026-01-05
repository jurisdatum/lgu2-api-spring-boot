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
    name = "extent",
    description = "The geographical extent",
    schema = @Schema(
        examples = {
            "uk",
            "england-only",
            "england-and-wales",
            "england-scotland-and-wales",
            "england-wales-and-northern-ireland",
            "england-and-scotland",
            "england-and-northern-ireland",
            "northern-ireland-only"
        }
    )

)
public @interface DefraExtent { }
