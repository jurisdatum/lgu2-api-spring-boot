package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

import java.util.List;
import java.util.Set;

public class ParameterValidator {

    public static void validateTitle(String title) {
        if (title != null && title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
    }

    private static final Set<String> GROUP_TYPES = Set.of(
        "primary", "secondary",
        "uk", "scotland", "wales",
        "ni", "all", "eu-origin"
    );

    public static void validateType(String type) {
        if (type == null) return;
        if (isUnknownType(type)) {
            throw new UnknownTypeException(type);
        }
    }

     //used it for search endpoint
    public static void validateType(List<String> types) {
        if (types == null || types.isEmpty()) return;

        for (String type : types) {
            if (type == null || isUnknownType(type)) {
                throw new UnknownTypeException(type);
            }
        }
    }

    private static boolean isUnknownType(String type) {
        return type.isBlank() || (!Types.isValidShortType(type) && !GROUP_TYPES.contains(type));
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
