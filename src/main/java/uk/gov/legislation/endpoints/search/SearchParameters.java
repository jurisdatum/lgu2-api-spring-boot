package uk.gov.legislation.endpoints.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.legislation.data.marklogic.search.Parameters;

import java.time.LocalDate;
import java.util.List;

public class SearchParameters {

    @JsonProperty("type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> types;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer year;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer startYear;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer endYear;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String number;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String subject;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String language;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDate published;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String q;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Parameters.Sort sort;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer page;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer pageSize;

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public LocalDate getPublished() {
        return published;
    }

    public void setPublished(LocalDate published) {
        this.published = published;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public Parameters.Sort getSort() {
        return sort;
    }

    public void setSort(Parameters.Sort sort) {
        this.sort = sort;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List <String> getTypes() {
        return types;
    }

    public void setTypes(List <String> types) {
        this.types = types;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
