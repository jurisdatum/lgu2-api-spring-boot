package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnsupportedLanguageException;

public class Language {

    public static String validateLanguage(String language) {
        if (!language.equals("en") && !language.equals("cy")) {
            throw new UnsupportedLanguageException("Unsupported Language, only (en and cy) is acceptable language: "
                + language + " is not acceptable");
        }
        return language;
    }

}
