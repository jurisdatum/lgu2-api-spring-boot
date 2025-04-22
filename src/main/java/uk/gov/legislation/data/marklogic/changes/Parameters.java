package uk.gov.legislation.data.marklogic.changes;

import uk.gov.legislation.data.marklogic.AbstractParameters;

public class Parameters extends AbstractParameters {

    public String affectedType;
    public Integer affectedYear;
    public Integer affectedNumber;
    public String affectedTitle;

    public String affectingType;
    public Integer affectingYear;
    public Integer affectingNumber;
    public String affectingTitle;

    public Integer page;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Parameters params = new Parameters();

        private Builder() { }

        public Builder affectedType(String affectedType) {
            params.affectedType = affectedType;
            return this;
        }

        public Builder affectedYear(Integer affectedYear) {
            params.affectedYear = affectedYear;
            return this;
        }

        public Builder affectedNumber(Integer affectedNumber) {
            params.affectedNumber = affectedNumber;
            return this;
        }

        public Builder affectedTitle(String affectedTitle) {
            params.affectedTitle = affectedTitle;
            return this;
        }

        public Builder affectingType(String affectingType) {
            params.affectingType = affectingType;
            return this;
        }

        public Builder affectingYear(Integer affectingYear) {
            params.affectingYear = affectingYear;
            return this;
        }

        public Builder affectingNumber(Integer affectingNumber) {
            params.affectingNumber = affectingNumber;
            return this;
        }

        public Builder affectingTitle(String affectingTitle) {
            params.affectingTitle = affectingTitle;
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
