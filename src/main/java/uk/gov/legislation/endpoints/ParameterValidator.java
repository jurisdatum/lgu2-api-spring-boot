package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

import java.util.Set;

public class ParameterValidator {

    public static void validateTitle(String title) {
        if (title != null && title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
    }

    public static final Set<String> OTHER_TYPES = Set.of(
        "primary", "secondary", "primary+secondary",
        "uk", "scotland", "wales", "ni",
        "all", "eu-origin"
    );

    public static void validateType(String type) {
        if (type == null)
            return;
        if (type.isBlank())
            throw new UnknownTypeException(type);
        if (Types.isValidShortType(type))
            return;
        if (OTHER_TYPES.contains(type))
            return;
        throw new UnknownTypeException(type);
    }

    // used only for query parameter for search endpoint
    public static void validateLanguage(String language) {
        if (language == null)
            return;
        if ("en".equals(language) || "cy".equals(language))
            return;
        throw new UnsupportedLanguageException(language);
    }

}
