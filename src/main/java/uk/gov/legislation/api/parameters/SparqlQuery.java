package uk.gov.legislation.api.parameters;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Parameter(
    description = "SPARQL query",
    example = """
                            PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
                            PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
                            PREFIX leg: <http://www.legislation.gov.uk/def/legislation/>

                            SELECT * WHERE {
                               ?s ?p ?o
                            }
                            LIMIT 10
                            """
)public @interface SparqlQuery {
}
