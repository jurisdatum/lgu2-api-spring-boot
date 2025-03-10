package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

public class ParameterValidator {

    public static void validateLanguage(String language) {
        if (!language.equals("en") && !language.equals("cy")) {
            throw new UnsupportedLanguageException("Unsupported Language, only (en and cy) is acceptable language: "
                    + language + " is not acceptable");
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
