package uk.gov.legislation.data.marklogic.changes;

public enum EffectsSort {

    AFFECTING_YEAR_NUMBER("affecting-year-number"),
    AFFECTING_TITLE("affecting-title"),
    AFFECTED_YEAR_NUMBER("affected-year-number"),
    AFFECTED_TITLE("affected-title"),
    APPLIED("applied");

    private final String value;

    EffectsSort(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
