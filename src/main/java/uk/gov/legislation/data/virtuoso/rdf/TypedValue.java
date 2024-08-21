package uk.gov.legislation.data.virtuoso.rdf;

import java.net.URI;

public interface TypedValue {

    public String value();

    public String type();

    public URI datatype();

    public String lang();

}
