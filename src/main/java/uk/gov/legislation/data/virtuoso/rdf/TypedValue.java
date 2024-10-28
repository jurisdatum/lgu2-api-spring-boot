package uk.gov.legislation.data.virtuoso.rdf;

import java.net.URI;

public interface TypedValue {

    String value();

    String type();

    URI datatype();

    String lang();

}
