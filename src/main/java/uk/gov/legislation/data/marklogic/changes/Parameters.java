package uk.gov.legislation.data.marklogic.changes;

import uk.gov.legislation.data.marklogic.AbstractParameters;

public class Parameters extends AbstractParameters {

    public String affectedType;
    public Integer affectedYear;
    public Integer affectedNumber;
    public Integer affectedStartYear;
    public Integer affectedEndYear;
    public String affectedTitle;
    public String affectingType;
    public Integer affectingYear;
    public Integer affectingNumber;
    public Integer affectingStartYear;
    public Integer affectingEndYear;
    public String affectingTitle;
    public AppliedStatus applied;
    public Integer page;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Parameters params = new Parameters();

        private Builder() {
        }

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
        public Builder affectedStartYear(Integer affectedStartYear) {
            params.affectedStartYear = affectedStartYear;
            return this;
        }
        public Builder affectedEndYear(Integer affectedEndYear) {
            params.affectedEndYear = affectedEndYear;
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
        public Builder affectingStartYear(Integer affectingStartYear) {
            params.affectingStartYear = affectingStartYear;
            return this;
        }
        public Builder affectingEndYear(Integer affectingEndYear) {
            params.affectingEndYear = affectingEndYear;
            return this;
        }

        public Builder affectingTitle(String affectingTitle) {
            params.affectingTitle = affectingTitle;
            return this;
        }

        public Builder applied(AppliedStatus appliedStatus) {
            params.applied = appliedStatus;
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
    public enum AppliedStatus {
        all, applied, unapplied
    }
}

