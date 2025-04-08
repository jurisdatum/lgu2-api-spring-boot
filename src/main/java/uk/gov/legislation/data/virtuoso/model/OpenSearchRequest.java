package uk.gov.legislation.data.virtuoso.model;



import java.util.List;

public class OpenSearchRequest {
    private String id;
    private String type;
    private int year;
    private int number;
    private String language;
    private List <String> title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List <String> getTitle() {
        return title;
    }

    public void setTitle(List <String> title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
