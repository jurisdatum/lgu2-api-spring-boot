package uk.gov.legislation.data.marklogic.legislation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Parameters {

    private final String type;
    private final String year;
    private final int number;
    private Optional<String> version;
    private Optional<String> view;
    private Optional<String> section;
    private Optional<String> lang;

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
        this.lang = Optional.empty();
    }

    public String type() { return type; }

    public String year() { return year; }

    public int number() { return number; }

    public Optional<String> version() { return version; }
    public Parameters version(Optional<String> version) {
        this.version = version;
        return this;
    }

    public Optional<String> view() { return view; }
    public Parameters view(Optional<String> view) {
        this.view = view;
        return this;
    }

    public Optional<String> section() { return section; }
    public Parameters section(Optional<String> section) {
        this.section = section;
        return this;
    }

    public Optional<String> lang() { return lang; }
    public Parameters lang(Optional<String> lang) {
        this.lang = lang;
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
        lang.ifPresent(s -> query.append("&lang=").append(encode(s)));
        return query.toString();
    }

    public static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

}
