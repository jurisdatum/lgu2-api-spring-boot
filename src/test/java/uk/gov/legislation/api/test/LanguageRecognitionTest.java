package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import uk.gov.legislation.endpoints.Application;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.legislation.endpoints.ParameterValidator.extractLanguage;

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
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", header);
        Locale locale = resolver.resolveLocale(request);
        assertEquals("en", locale.getLanguage());
    }

    @Test
    void fr_cy_en() {
        String header = "fr,cy,en";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Accept-Language", header);
        Locale locale = resolver.resolveLocale(request);
        assertEquals("cy", locale.getLanguage());
    }
    @Test
    void testValidEnglishHeader() {
        String result = extractLanguage("en");
        assertEquals("en", result);
    }

    @Test
    void testValidWelshHeader() {
        String result = extractLanguage("cy");
        assertEquals("cy", result);
    }

    @Test
    void testValidMultipleLanguagesWithPriority() {
        String result = extractLanguage("cy,en;q=0.8");
        assertEquals("cy", result);
    }

    @Test
    void testFallbackToSecondSupportedLanguage() {
        String result = extractLanguage("fr,cy;q=0.9,en;q=0.8");
        assertEquals("cy", result);
    }

    @Test
    void testFallbackToThirdSupportedLanguage() {
        String result = extractLanguage("fr,de,en;q=0.5");
        assertEquals("en", result);
    }

    @Test
    void testNotSupportedLanguage() {
        UnsupportedLanguageException exception = assertThrows(
                UnsupportedLanguageException.class,
                () -> extractLanguage("fr,de")
        );
        assertEquals("Unsupported language: fr,de", exception.getMessage());
    }

    @Test
    void testNullHeader() {
        UnsupportedLanguageException exception = assertThrows(
                UnsupportedLanguageException.class,
                () -> extractLanguage(null)
        );
        assertEquals("Unsupported language: null", exception.getMessage());
    }
}
