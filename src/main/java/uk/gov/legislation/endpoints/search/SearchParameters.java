package uk.gov.legislation.endpoints.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.api.parameters.Sort;
import uk.gov.legislation.data.marklogic.search.Parameters;
import uk.gov.legislation.util.Extent;

import java.time.LocalDate;
import java.util.List;

/**
 * Search parameters for legislation search endpoints.
 * <p>
 * Used as a {@code @ParameterObject} for Spring parameter binding from query parameters
 * and for JSON serialization/deserialization. Note the special naming convention:
 * {@code getTypes()}/{@code setType()} to handle the "type" parameter that can have multiple values.
 */
public class SearchParameters {

    private List<String> types;
    private Integer year;
    private Integer startYear;
    private Integer endYear;
    private String number;
    private String title;
    private String subject;
    private String language;
    private LocalDate published;
    private String q;
    private Parameters.Sort sort;
    private List<Extent> extent;
    private boolean exclusive;
    private Integer page;
    private Integer pageSize;

    @JsonProperty("type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> getTypes() {
        return types;
    }

    public void setType(List<String> types) {
        this.types = types;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDate getPublished() {
        return published;
    }

    public void setPublished(LocalDate published) {
        this.published = published;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Sort
    public Parameters.Sort getSort() {
        return sort;
    }

    public void setSort(Parameters.Sort sort) {
        this.sort = sort;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Extent> getExtent() { return extent; }

    public void setExtent(List<Extent> extent) { this.extent = extent; }

    public boolean isExclusive() { return exclusive; }

    public void setExclusive(boolean exclusive) { this.exclusive = exclusive; }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(defaultValue = "1")
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(defaultValue = "20")
    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
