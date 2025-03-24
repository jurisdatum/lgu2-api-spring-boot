package uk.gov.legislation.data.virtuoso.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.legislation.data.virtuoso.model.Resources.Leg;
import uk.gov.legislation.data.virtuoso.model.Resources.RDF;
import uk.gov.legislation.data.virtuoso.rdf.RdfProperty;

import java.net.URI;
import java.util.List;
import java.util.Optional;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {

    @JsonProperty(index = 0)
    public URI uri;

    @RdfProperty(value = RDF.Type)
    @JsonIgnore
    public List<String> types;

    @JsonProperty(value = "type", index = 1)
    public String type() {
        Optional<String> type = types.stream().filter(t -> Resources.Leg.DocumentTypes.contains(t)).findFirst();
        if (type.isEmpty())
            return null;
        return type.get().substring(Resources.Leg.Prefix.length());
    }

    @RdfProperty(Leg.Year)
    public int year;

    @RdfProperty(Leg.Number)
    public int number;

    @RdfProperty(Leg.Title)
    public String title;

    @RdfProperty(Leg.Citation)
    public String citation;

    @RdfProperty(Leg.FullCitation)
    public String fullCitation;

    @RdfProperty(Leg.CommentaryCitation)
    public String commentaryCitation;

    @RdfProperty(Leg.OriginalLanguage)
    public String originalLanguage;

    public List<Interpretation> interpretations;

}
