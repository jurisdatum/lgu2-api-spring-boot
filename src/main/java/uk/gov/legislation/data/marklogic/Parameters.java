package uk.gov.legislation.data.marklogic;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Parameters {

    private final String type;
    private final String year;
    private final int number;
    private Optional<String> version;
    Optional<String> view;
    Optional<String> section;

    public Parameters(String type, String year, int number) {
        if (type == null)
            throw new IllegalArgumentException();
        if (year == null)
            throw new IllegalArgumentException();
        this.type = type;
        this.year = year;
        this.number = number;
        this.version = Optional.empty();
        this.view = Optional.empty();
        this.section = Optional.empty();
    }

    public String type() { return type; }

    public String year() { return year; }

    public int number() { return number; }

    public Optional<String> version() { return version; }

    public Parameters version(Optional<String> version) {
        this.version = version;
        return this;
    }

    public Parameters view(Optional<String> view) {
        this.view = view;
        return this;
    }

    public Parameters section(Optional<String> section) {
        this.section = section;
        return this;
    }

    public String buildQuery() {
        StringBuilder query = new StringBuilder();
        query.append("?type=").append(encode(type))
                .append("&year=").append(encode(year))
                .append("&number=").append(number);
        version.ifPresent(v -> query.append("&version=").append(encode(v)));
        view.ifPresent(v -> query.append("&view=").append(encode(v)));
        section.ifPresent(s -> query.append("&section=").append(encode(s)));
        return query.toString();
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }
}
