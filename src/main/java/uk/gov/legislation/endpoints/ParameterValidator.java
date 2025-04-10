package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

import java.util.List;
import java.util.Locale;

public class ParameterValidator {

    public static String extractLanguage(String language) {
        if (language == null) {
            throw new UnsupportedLanguageException("Unsupported language: null");
        }

        List<Locale> supportedLocales = List.of(
                Locale.forLanguageTag("en"),
                Locale.forLanguageTag("cy")
        );

        List<Locale.LanguageRange> languageRanges = Locale.LanguageRange.parse(language);
        Locale matchedLocale = Locale.lookup(languageRanges, supportedLocales);

        return matchedLocale.getLanguage();
    }


    public static void validateTitle(String title) {
        if (title != null && title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
    }

    public static void validateType(String type) {
        if (type == null)
            return;
        if (type.isBlank())
            throw new UnknownTypeException(type);
        if (!Types.isValidShortType(type))
            throw new UnknownTypeException(type);
    }

}
