package uk.gov.legislation.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class Configuration {

    @Value("${stylesheet.simplify.path}")
    private String stylesheetSimplifyPath;

    @Value("${stylesheet.akn.path}")
    private String stylesheetAknPath;

}
