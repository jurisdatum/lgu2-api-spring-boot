package uk.gov.legislation.data.marklogic.search;

import uk.gov.legislation.data.marklogic.AbstractParameters;
import uk.gov.legislation.util.Type;

public class Parameters extends AbstractParameters {

    // could be an enum
    public String series;

    public String type;

    public Integer year;

    public Sort sort;

    public Integer page;

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

        public Builder year(int year) {
            params.year = year;
            return this;
        }

        public Builder sort(Sort sort) {
            params.sort = sort;
            return this;
        }

        public Builder page(int page) {
            params.page = page;
            return this;
        }

        public Parameters build() {
            return params;
        }

    }

}
