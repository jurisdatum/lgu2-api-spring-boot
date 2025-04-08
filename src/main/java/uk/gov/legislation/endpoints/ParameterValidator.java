package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

import java.util.List;
import java.util.Locale;

public class ParameterValidator {

    public static String extractLanguage(Locale locale) {
        String language = locale.getLanguage();
        if ("en".equals(language) || "cy".equals(language))
            return language;
        throw new UnsupportedLanguageException("Unsupported language: " + language);
    }

    @Deprecated(forRemoval = true)
    public static void validateLanguage(String acceptLanguageHeader) {
        final List<Locale> supportedLocales = List.of(
                Locale.forLanguageTag("en"),
                Locale.forLanguageTag("cy"),
                Locale.forLanguageTag("en-GB"),
                Locale.forLanguageTag("cy-GB")
        );

        List<Locale.LanguageRange> ranges = Locale.LanguageRange.parse(acceptLanguageHeader);

        boolean isValid = ranges.stream()
                .anyMatch(range -> {
                    String code = range.getRange().toLowerCase(Locale.ROOT);
                    return supportedLocales.stream()
                            .anyMatch(locale ->
                                    code.equals(locale.toLanguageTag()) ||
                                            code.startsWith(locale.getLanguage() + "-")
                            );
                });

        if (!isValid) {
            throw new UnsupportedLanguageException(
                    "Unsupported language. Supported: " +
                            supportedLocales.stream().map(Locale::toLanguageTag).toList()
            );
        }
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
