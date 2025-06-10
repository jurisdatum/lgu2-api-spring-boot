package uk.gov.legislation.data.virtuoso.defra;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.legislation.data.virtuoso.jsonld.ValueAndLanguage;
import uk.gov.legislation.data.virtuoso.jsonld.ValueAndType;

import java.net.URI;

@Deprecated(forRemoval = true)
public class DefraItem {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public URI rdfType;

    @JsonProperty
    public ValueAndType modified;

    @JsonProperty
    public URI chapter;

    @JsonProperty
    public Boolean complete;

    @JsonProperty
    public URI extent;

    @JsonProperty
    public URI heading;

    @JsonProperty
    public Boolean inforce;

    @JsonProperty("isCategory_complete")
    public Boolean isCategoryComplete;

    @JsonProperty("isDetails_complete")
    public Boolean isDetailsComplete;

    @JsonProperty
    public URI isRegulatedBy;

    @JsonProperty("isRevoke_complete")
    public Boolean isRevokeComplete;

    @JsonProperty("legislativecontents")
    public URI legislativeContents;

    @JsonProperty
    public URI leguri;

    @JsonProperty
    public ValueAndType sortby;

    @JsonProperty
    public URI sourceOrigin;

    @JsonProperty
    public URI status;

    @JsonProperty("type")  // distinct from "@type"
    public String legType;

    @JsonProperty
    public String number;

    @JsonProperty
    public ValueAndLanguage title;

    @JsonProperty
    public URI within;

    @JsonProperty
    public ValueAndType year;

}
