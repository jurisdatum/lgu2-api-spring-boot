package uk.gov.legislation.data.marklogic;

import lombok.Getter;

@Getter
class RedirectException extends Exception {
    private final String location;

    RedirectException(String location) {
        super("Redirect to: " + location);
        this.location = location;
    }

}
