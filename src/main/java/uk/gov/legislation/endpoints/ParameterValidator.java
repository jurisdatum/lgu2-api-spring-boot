package uk.gov.legislation.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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

    private static final Set<String> STAGES = Set.of(
        "Consultation",
        "Development",
        "Enactment",
        "Final",
        "Implementation",
        "Options",
        "Post Implementation"
    );

    public static void validateStage(String stage) {
        if (stage == null)
            return;
        if (STAGES.stream().noneMatch(s -> s.equalsIgnoreCase(stage)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unrecognized stage: " + stage);
    }

}
