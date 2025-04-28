package uk.gov.legislation.data.virtuoso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.legislation.data.virtuoso.Resources.Leg;
import uk.gov.legislation.data.virtuoso.Resources.RDF;
import uk.gov.legislation.data.virtuoso.rdf.RdfProperty;

import java.net.URI;
import java.util.List;

@Deprecated(forRemoval = true)
public class Interpretation {

    public URI uri;

    @RdfProperty(RDF.Type)
    @JsonIgnore
    public List<String> types;

    @RdfProperty(Leg.Language)
    public String language;

    @RdfProperty(Leg.LongTitle)
    public String longTitle;

    @RdfProperty(Leg.ShortTitle)
    public String shortTitle;

    @JsonProperty
    public boolean original() {
        return types.stream().anyMatch(t -> t.equals(Leg.OriginalInterpretation));
    }

    @JsonProperty
    public boolean current() {
        return types.stream().anyMatch(t -> t.equals(Leg.CurrentInterpretation));
    }

}
