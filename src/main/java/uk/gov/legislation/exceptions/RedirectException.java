package uk.gov.legislation.exceptions;

import lombok.Getter;

@Getter
public class RedirectException extends Exception {
    private final String location;

    public RedirectException(String location) {
        super("Redirect to: " + location);
        this.location = location;
    }

}
