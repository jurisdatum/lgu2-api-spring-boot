package uk.gov.legislation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

    @Value("${stylesheet.simplify.path}")
    private String stylesheetSimplifyPath;

    @Value("${stylesheet.akn.path}")
    private String stylesheetAknPath;

    public String getStylesheetSimplifyPath() {
        return stylesheetSimplifyPath;
    }

    public String getStylesheetAknPath() {
        return stylesheetAknPath;
    }
}
