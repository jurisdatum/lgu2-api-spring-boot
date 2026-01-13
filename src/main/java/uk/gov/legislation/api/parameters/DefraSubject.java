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
    name = "subject",
    description = "The subject of the legislation",
    examples = {
        @ExampleObject(value="criminal-offences", summary="Criminal Offences"),
        @ExampleObject(value="notice-order-direction-declaration", summary="Notice/Order/Direction/Declaration"),
        @ExampleObject(value="grant-subsidy-payment", summary="Grant/Subsidy/Payment"),
        @ExampleObject(value="powers-of-entry", summary="Powers of Entry"),
        @ExampleObject(value="incoming-fees-and-payments", summary="Incoming Fees and Payments"),
        @ExampleObject(value="consents-and-permits", summary="Consents and Permits"),
        @ExampleObject(value="licences", summary="Licences"),
        @ExampleObject(value="appeals", summary="Appeals"),
        @ExampleObject(
            value="non-criminal-penalties-including-administrative-penalties-and-civil-sanctions",
            summary="Non-Criminal Penalties (Including Administrative Penalties and Civil Sanctions)"
        ),
        @ExampleObject(value="registers", summary="Registers"),
        @ExampleObject(
            value="plans-guidance-assessments-information-schemes",
            summary="Plans/Guidance/Assessments/Information/Schemes"
        )
    }
)
public @interface DefraSubject { }
