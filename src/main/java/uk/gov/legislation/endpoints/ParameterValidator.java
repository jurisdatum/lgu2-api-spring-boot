package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

public class ParameterValidator {


    public static void validateLanguage(String languageHeader) {

        String[] languages = languageHeader.split(",");

        for (String language : languages) {
            String lang = language.split(";")[0].trim();
            /// Only compare the base language tag (e.g., "en-US" -> "en")
            if (lang.startsWith("en") || lang.startsWith("cy")) {
                return;
            }
        }

        throw new UnsupportedLanguageException(
                "Unsupported Language: '" + languageHeader + "'. Only 'en' and 'cy' are supported.");

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
