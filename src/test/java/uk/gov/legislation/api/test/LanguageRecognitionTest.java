package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import uk.gov.legislation.endpoints.Application;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
class LanguageRecognitionTest {

    final AcceptHeaderLocaleResolver resolver;

    @Autowired
    public LanguageRecognitionTest(LocaleResolver resolver) {
        this.resolver = (AcceptHeaderLocaleResolver) resolver;
    }

    @Test
    void fr_en() {
        String header = "fr,en";
        String language = extractLanguageFromAcceptLanguageHeader(header);
        assertEquals("en", language);
    }

    @Test
    void fr_cy_en() {
        String header = "fr,cy,en";
        String language = extractLanguageFromAcceptLanguageHeader(header);
        assertEquals("cy", language);
    }

    @Test
    void testValidEnglishHeader() {
        String result = extractLanguageFromAcceptLanguageHeader("en");
        assertEquals("en", result);
    }

    @Test
    void testValidWelshHeader() {
        String result = extractLanguageFromAcceptLanguageHeader("cy");
        assertEquals("cy", result);
    }

    @Test
    void testValidMultipleLanguagesWithPriority() {
        String result = extractLanguageFromAcceptLanguageHeader("cy,en;q=0.8");
        assertEquals("cy", result);
    }

    @Test
    void testFallbackToSecondSupportedLanguage() {
        String result = extractLanguageFromAcceptLanguageHeader("fr,cy;q=0.9,en;q=0.8");
        assertEquals("cy", result);
    }

    @Test
    void testFallbackToThirdSupportedLanguage() {
        String result = extractLanguageFromAcceptLanguageHeader("fr,de,en;q=0.5");
        assertEquals("en", result);
    }

    @Test
    void testNotSupportedLanguage() {
        String result = extractLanguageFromAcceptLanguageHeader("fr,de");
        assertEquals("en", result);
    }

    /* helper */

    private String extractLanguageFromAcceptLanguageHeader(String header) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", header);
        Locale locale = resolver.resolveLocale(request);
        return locale.getLanguage();
    }

}
