package uk.gov.legislation.api.documents;

import java.time.LocalDateTime;
import java.util.List;

public class Response {
    public Meta meta;
    public List<Document> documents;

public static class Meta {
    public int page;
    public int pageSize;
    public int totalPages;
    public LocalDateTime updated;
    public Counts counts;
}

public static class Counts {
    public int total;
    public List<Yearly> yearly;
}

public static class Yearly {
    public int year;
    public int count;
}

public static class Document {
    public String id;
    public String title;
    public String longType;
    public String year;
    public String number;
    public String created;
    public String version;
}

}
