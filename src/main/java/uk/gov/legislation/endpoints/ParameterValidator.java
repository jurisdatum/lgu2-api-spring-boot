package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

import java.util.*;

public class ParameterValidator {

    public static void validateTitle(String title) {
        if(title != null && title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank.");
        }
    }

    private static final Set <String> GROUP_TYPES = Set.of("primary", "secondary", "primary+secondary",  // for convenience of front-end
        "uk", "scotland", "wales", "ni", "eu-origin"
        // "drafts", "impacts"
    );

    public static void validateType(String type) {
        if(type == null)
            return;
        if(isUnknownType(type)) {
            throw new UnknownTypeException(type);
        }
    }

    // used for search endpoint
    public static void validateType(List <String> types) {
        if(types == null || types.isEmpty())
            return;

        for(String type : types) {
            if(isUnknownType(type)) {
                throw new UnknownTypeException(type);
            }
        }
    }

    private static boolean isUnknownType(String type) {
        return !Types.isValidShortType(type) && !GROUP_TYPES.contains(type);
    }

    // used only for query parameter for search endpoint
    public static void validateLanguage(String language) {
        if(language == null)
            return;
        if("en".equals(language) || "cy".equals(language))
            return;
        throw new UnsupportedLanguageException(language);
    }

    public static String validateExtent(List <String> extentList) {
        if(extentList == null || extentList.isEmpty())
            return null;

        boolean isExclusive = false;
        List <String> cleanList = new ArrayList <>();

        for(String input : extentList) {
            String trimmed = input.trim();
            String lower = trimmed.toLowerCase();

            if(lower.equals("only")) {
                isExclusive = true;
            }
            else {
                cleanList.add(trimmed);
            }
        }
        LinkedHashSet <String> result = getValidExtent(cleanList);
        String finalResult = String.join("+", result);
        return isExclusive ? "=" + finalResult : finalResult;
    }

    private static LinkedHashSet <String> getValidExtent(List <String> cleanList) {
        Map <String, String> validMap = Map.ofEntries(
            Map.entry("england", "E"),
            Map.entry("E", "E"),

            Map.entry("wales", "W"),
            Map.entry("W", "W"),

            Map.entry("scotland", "S"),
            Map.entry("S", "S"),

            Map.entry("ni", "N.I."),
            Map.entry("N.I.", "N.I."));

        LinkedHashSet <String> result = new LinkedHashSet <>();

        for(String input : cleanList) {
            String code = validMap.get(input);
            if(code == null) {
                throw new IllegalArgumentException("Invalid extent value: " + input);
            }
            result.add(code);
        }
        return result;
    }
}

