package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Extent;
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

    /**
     * Validate a list of Extent enums and return a formatted string.
     * If isExclusive is true, prefix with '='.
     */
    public static String validateExtent(List<Extent> list, boolean isExclusive) {
        if (list == null || list.isEmpty()) return null;

        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (Extent e : list) {
            if (e == null) throw new IllegalArgumentException("Extent cannot be null");
            set.add(e.code());
        }

        String joined = String.join("+", set);
        return isExclusive ? "=" + joined : joined;
    }

}



