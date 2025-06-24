package uk.gov.legislation.endpoints.search;

import com.fasterxml.jackson.annotation.JsonInclude;
import uk.gov.legislation.data.marklogic.search.Parameters;

import java.time.LocalDate;
import java.util.List;

public class SearchParameters {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> types;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer year;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer startYear;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer endYear;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer number;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String title;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String language;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public LocalDate published;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    public String orderBy;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer page;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer pageSize;

    public static SearchParameters.Builder builder() { return new SearchParameters.Builder(); }

    public static class Builder {

        private final SearchParameters params = new SearchParameters();

        private Builder() { }

        public SearchParameters.Builder types(List<String> types) {
            params.types = types;
            return this;
        }

        public SearchParameters.Builder year(Integer year) {
            params.year = year;
            return this;
        }

        public SearchParameters.Builder startYear(Integer year) {
            params.startYear = year;
            return this;
        }

        public SearchParameters.Builder endYear(Integer year) {
            params.endYear = year;
            return this;
        }

        public SearchParameters.Builder number(Integer num) {
            params.number = num;
            return this;
        }

        public SearchParameters.Builder title(String title) {
            params.title = title;
            return this;
        }

        public SearchParameters.Builder language(String language) {
            params.language = language;
            return this;
        }

        public SearchParameters.Builder published(LocalDate published) {
            params.published = published;
            return this;
        }

//        public SearchParameters.Builder orderBy(String orderBy) {
//            params.orderBy = orderBy;
//            return this;
//        }

        public SearchParameters.Builder page(Integer page) {
            params.page = page;
            return this;
        }

        public SearchParameters.Builder pageSize(Integer pageSize) {
            params.pageSize = pageSize;
            return this;
        }

        public SearchParameters build() {
            return params;
        }

    }

    public Parameters convert() {
        return Parameters.builder()
            .type(types)
            .year(year)
            .startYear(startYear)
            .endYear(endYear)
            .number(number)
            .title(title)
            .language(language)
            .published(published)
//            .sort("published".equals(orderBy) ? Parameters.Sort.PUBLISHED : null)
            .page(page)
            .pageSize(pageSize)
            .build();
    }

}
