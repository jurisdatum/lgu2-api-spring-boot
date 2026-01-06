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
    name = "regulator",
    description = "The Defra regulator",
    examples = {
        @ExampleObject(value="la", summary="Local Authorities"),
        @ExampleObject(value="secretary-of-state-defra", summary="Secretary of State (Defra)"),
        @ExampleObject(value="ea", summary="Environment Agency"),
        @ExampleObject(
            value="hpha",  // This may be a data issue?
            summary="Animal and Plant Health Agency"
        ),
        @ExampleObject(value="mmo", summary="Marine Management Organisation"),
        @ExampleObject(value="rpa", summary="Rural Payments Agency"),
        @ExampleObject(value="ne", summary="Natural England"),
        @ExampleObject(value="fsa", summary="Food Standards Agency"),
        @ExampleObject(value="fc", summary="Forestry Commission"),
        @ExampleObject(value="cefas", summary="Centre for Environment, Fisheries and Aquaculture Science"),
        @ExampleObject(value="hse", summary="Health and Safety Executive"),
        @ExampleObject(value="ofwat", summary="Ofwat (Water Services Regulation Authority)"),
        @ExampleObject(value="vmd", summary="Veterinary Medicines Directorate"),
        @ExampleObject(value="dwi", summary="Drinking Water Inspectorate")
    }
)
public @interface DefraRegulator { }
