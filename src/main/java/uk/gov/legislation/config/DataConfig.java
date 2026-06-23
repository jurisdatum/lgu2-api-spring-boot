package uk.gov.legislation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.legislation.data.marklogic.MarkLogicConfig;
import uk.gov.legislation.data.virtuoso.VirtuosoConfig;
import uk.gov.legislation.data.virtuoso.defra.DefraLexConfig;

/**
 * Binds external properties to the framework-agnostic data-layer config records. All Spring
 * coupling for data-source configuration lives here, keeping {@code uk.gov.legislation.data} free
 * of configuration plumbing.
 */
@Configuration
public class DataConfig {

    @Bean
    MarkLogicConfig markLogicConfig(
            @Value("${MARKLOGIC_HOST}") String host,
            @Value("${MARKLOGIC_PORT}") int port,
            @Value("${MARKLOGIC_USERNAME}") String username,
            @Value("${MARKLOGIC_PASSWORD}") String password) {
        return new MarkLogicConfig(host, port, username, password);
    }

    @Bean
    VirtuosoConfig virtuosoConfig(
            @Value("${VIRTUOSO_HOST}") String host, @Value("${VIRTUOSO_PORT}") int port) {
        return new VirtuosoConfig(host, port);
    }

    @Bean
    DefraLexConfig defraLexConfig(
            @Value("${VIRTUOSO_HOST}") String host, @Value("${DEFRALEX_PORT}") int port) {
        return new DefraLexConfig(host, port);
    }
}
