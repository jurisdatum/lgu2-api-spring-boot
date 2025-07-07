package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Alphabet;
import uk.gov.legislation.util.Types;

import java.util.*;

public class ParameterValidator {

    public static void validateTitle(String title) {
        if (title != null && title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
    }

    private static final Set<String> GROUP_TYPES = Set.of(
        "primary", "secondary",
        "primary+secondary",  // for convenience of front-end
        "uk", "scotland", "wales", "ni",
        "eu-origin"
        // "drafts", "impacts"
    );

    public static void validateType(String type) {
        if (type == null) return;
        if (isUnknownType(type)) {
            throw new UnknownTypeException(type);
        }
    }

    // used for search endpoint
    public static void validateType(List<String> types) {
        if (types == null || types.isEmpty()) return;

        for (String type : types) {
            if (isUnknownType(type)) {
                throw new UnknownTypeException(type);
            }
        }
    }

    private static boolean isUnknownType(String type) {
        return !Types.isValidShortType(type) && !GROUP_TYPES.contains(type);
    }

    // used only for query parameter for search endpoint
    public static void validateLanguage(String language) {
        if (language == null)
            return;
        if ("en".equals(language) || "cy".equals(language))
            return;
        throw new UnsupportedLanguageException(language);
    }

    public static String validateSubject(Alphabet initialLetter, String heading) {
        if (initialLetter != null) {
            return initialLetter.name();
        }
        if (heading == null || heading.isBlank()) {
            return null;
        }
        String trimmed = heading.trim();
        if (!trimmed.matches("^[A-Z]+(\\s+[A-Z]+)*$")) {
            return null;
        }
        return trimmed;
    }
}
