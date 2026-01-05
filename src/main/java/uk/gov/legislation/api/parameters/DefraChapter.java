package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    name = "chapter",
    description = "The category of legislation",
    examples = {
        @ExampleObject(value="environment", summary="Environment"),
        @ExampleObject(value="agriculture-and-rural-development", summary="Agriculture and Rural Development"),
        @ExampleObject(value="animal-health-and-welfare", summary="Animal Health and Welfare"),
        @ExampleObject(value="marine-and-fisheries", summary="Marine and Fisheries"),
        @ExampleObject(value="plant-health-and-quality", summary="Plant Health and Quality"),
        @ExampleObject(value="food-labelling-and-composition", summary="Food Labelling and Composition"),
        @ExampleObject(value="other-issues", summary="Other Issues")
    }
)
public @interface DefraChapter { }
