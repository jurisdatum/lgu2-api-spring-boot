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

}
