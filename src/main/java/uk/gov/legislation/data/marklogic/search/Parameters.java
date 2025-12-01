package uk.gov.legislation.data.marklogic.search;

import uk.gov.legislation.data.marklogic.AbstractParameters;

import java.time.LocalDate;
import java.util.List;

public class Parameters extends AbstractParameters {

    public String type;

    public Integer year;

    public Integer startYear;

    public Integer endYear;

    public Integer number;

    public Series series;

    public String title;

    public String subject;

    public String language;

    public LocalDate published;

    public String text;

    public Sort sort;

    public String extent;

    public String stage;

    public String department;

    public Integer page;

    public Integer resultsCount; // page size

    public enum Sort {

        RELEVANCE,
        PUBLISHED,
        TITLE,
//        YEAR,
        TYPE;


        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public enum Series {

        W, S, C, L, NI;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private final Parameters params = new Parameters();

        public Builder() { }

        public Builder type(String type) {
            params.type = type;
            return this;
        }

        public Builder type(List<String> types) {
            if (types == null || types.isEmpty()) {
                params.type = null;
            } else {
                params.type = String.join("+", types);
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

        public Builder number(Integer num, Series series) {
            params.number = num;
            params.series = num == null ? null : series;
            return this;
        }

        public Builder title(String title) {
            params.title = title;
            return this;
        }

        public Builder subject(String subject) {
            params.subject = subject;
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

        public Builder text(String text) {
            params.text = text;
            return this;
        }

        public Builder sort(Sort sort) {
            params.sort = sort;
            return this;
        }
        public Builder extent(String extent) {
            params.extent = extent;
            return this;
        }

        public Builder stage(String stage) {
            params.stage = stage;
            return this;
        }

        public Builder department(String department) {
            params.department = department;
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
