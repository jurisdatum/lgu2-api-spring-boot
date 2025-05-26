package uk.gov.legislation.data.virtuoso.jsonld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;


public class RegnalLD {

    @JsonProperty("@id")
    public URI id;

    @JsonProperty("@type")
    public String type;

    @JsonProperty
    public String label;

    @JsonProperty
    public URI endCalendarYear;

    @JsonProperty
    public URI startCalendarYear;

    @JsonProperty
    public Integer yearOfReign;

    @JsonProperty
    public URI endDate;

    @JsonProperty
    public List<URI> overlapsCalendarYear;

    @JsonProperty
    public URI reign;

    @JsonProperty
    public URI startDate;

    public Integer getEndCalendarYear() {
        return extractYearFromUri(endCalendarYear);
    }

    public Integer getStartCalendarYear() {
        return extractYearFromUri(startCalendarYear);
    }

    public LocalDate getEndDate() {
        return extractDateFromUri(endDate);
    }

    public LocalDate getStartDate() {
        return extractDateFromUri(startDate);
    }

    public List<Integer> getOverlapsCalendarYear() {
        return overlapsCalendarYear.stream()
            .map(this::extractYearFromUri)
            .toList();
    }

    private Integer extractYearFromUri(URI uri) {
        if (uri == null) return null;
        String path = uri.getPath();
        String[] parts = path.split("/");
        String lastPart = parts[parts.length - 1];
        return Integer.parseInt(lastPart);
    }

    private LocalDate extractDateFromUri(URI uri) {
        if (uri == null) return null;
        String path = uri.getPath();
        String[] parts = path.split("/");
        String dateStr = parts[parts.length - 1];
        return LocalDate.parse(dateStr);
    }

    public static RegnalLD convert(ObjectNode node) {
        return Graph.mapper.convertValue(node, RegnalLD.class);
    }
}



