package uk.gov.legislation.data.marklogic.impacts;

import uk.gov.legislation.data.marklogic.AbstractParameters;

public class ImpactsParameters extends AbstractParameters {

    String type;

    String year;

    String number;

    String impacttype;

    String impactyear;

    String impactnumber;

    public static Builder builder() { return new Builder(); }

    public static class Builder {

        private final ImpactsParameters params = new ImpactsParameters();

        private Builder() { }

        public Builder type(String type) {
            params.type = type;
            return this;
        }

        public Builder year(int year) {
            params.year = Integer.toString(year);
            return this;
        }

        public Builder number(int number) {
            params.number = Integer.toString(number);
            return this;
        }

    }

}
