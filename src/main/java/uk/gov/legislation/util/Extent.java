package uk.gov.legislation.util;

public enum Extent {
    E("E"),
    W("W"),
    S("S"),
    NI("N.I.");
    private final String code;

    Extent(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}

