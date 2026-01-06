package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Parameter(
    description = "Monarch's reign identifier (e.g., 'Eliz1' for Queen Elizabeth I, 'Geo6' for George VI)",
    example = "Eliz1"
)
public @interface Reign {
}
