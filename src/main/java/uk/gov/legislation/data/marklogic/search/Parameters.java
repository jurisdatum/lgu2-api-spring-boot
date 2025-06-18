package uk.gov.legislation.data.marklogic.search;

import uk.gov.legislation.data.marklogic.AbstractParameters;
import uk.gov.legislation.util.Type;

import java.time.LocalDate;

public class Parameters extends AbstractParameters {

    // could be an enum
    public String series;

    public String type;

    public Integer year;

    public Integer startYear;

    public Integer endYear;

    public Integer number;

    public String title;

    public String language;

    public LocalDate published;

    public Sort sort;

    public Integer page;

    public Integer resultsCount; // page size

    public enum Sort {

        RELEVANCE,
        PUBLISHED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private final Parameters params = new Parameters();

        private Builder() { }

        public Builder type(String type) {
            params.type = type;
            if (Type.WSI.shortName().equals(type)) {
                params.series = "w";
            } else if (Type.NISI.shortName().equals(type)) {
                params.series = "ni";
            }
            return this;
        }

        public Builder year(Integer year) {
            params.year = year;
            return this;
        }

        public Builder startYear(Integer year) {
            params.startYear = year;
            return this;
        }

        public Builder endYear(Integer year) {
            params.endYear = year;
            return this;
        }

        public Builder number(Integer num) {
            params.number = num;
            return this;
        }

        public Builder title(String title) {
            params.title = title;
            return this;
        }

        public Builder language(String language) {
            params.language = language;
            return this;
        }

        public Builder published(LocalDate published) {
            params.published = published;
            return this;
        }

        public Builder sort(Sort sort) {
            params.sort = sort;
            return this;
        }

        public Builder page(Integer page) {
            params.page = page;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            params.resultsCount = pageSize;
            return this;
        }

        public Parameters build() {
            return params;
        }

    }

}
