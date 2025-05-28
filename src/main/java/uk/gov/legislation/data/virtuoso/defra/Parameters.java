package uk.gov.legislation.data.virtuoso.defra;

import uk.gov.legislation.data.marklogic.AbstractParameters;

public class Parameters extends AbstractParameters {

    public static final int DEFAULT_PAGE = 1;

    public static final int DEFAULT_PAGE_SIZE = 20;

    public String status;

    public Integer year;

    public String type;

    public String chapter;

    public String extent;

    public String source;

    public String regulator;

    public String subject; // "content"

    public Integer review; // a year

    public Integer page;

    public Integer pageSize;


    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private final Parameters params = new Parameters();

        private Builder() {
        }

        public Builder status(String status) {
            params.status = status;
            return this;
        }

        public Builder year(Integer year) {
            params.year = year;
            return this;
        }

        public Builder type(String type) {
            params.type = type;
            return this;
        }

        public Builder chapter(String chapter) {
            params.chapter = chapter;
            return this;
        }

        public Builder extent(String extent) {
            params.extent = extent;
            return this;
        }

        public Builder source(String source) {
            params.source = source;
            return this;
        }

        public Builder regulator(String regulator) {
            params.regulator = regulator;
            return this;
        }

        public Builder subject(String subject) {
            params.subject = subject;
            return this;
        }

        public Builder review(Integer review) {
            params.review = review;
            return this;
        }

        public Builder page(Integer page) {
            params.page = page;
            return this;
        }

        public Builder pageSize(Integer pageSize) {
            params.pageSize = pageSize;
            return this;
        }

        public Parameters build() {
            return params;
        }

    }

}
