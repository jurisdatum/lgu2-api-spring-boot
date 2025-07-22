package uk.gov.legislation.endpoints;

import uk.gov.legislation.exceptions.UnknownTypeException;
import uk.gov.legislation.exceptions.UnsupportedLanguageException;
import uk.gov.legislation.util.Types;

import java.time.LocalDate;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

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

    public static String validateExtent(List<String> extentList, boolean isExclusivelyExtends) {
        if (extentList == null || extentList.isEmpty()) return null;

        Map<String, String> validMap = Map.of(
            "england", "E", "E", "E",
            "wales", "W", "W", "W",
            "scotland", "S", "S", "S",
            "ni", "N.I.", "N.I.", "N.I."
        );
        LinkedHashSet<String> result = new LinkedHashSet<>();

        for (String input : extentList) {
            String trimmed = input.trim();
            String code = validMap.get(trimmed);
            if (code == null) {
                throw new IllegalArgumentException("Invalid extent value: " + trimmed);
            }
            result.add(code);
        }
        String finalResult = String.join("+", result);

        if (isExclusivelyExtends) {
            finalResult = "=" + finalResult;
        }
        return finalResult;
    }


    public static boolean areAllParamsEmpty(
        String title,
        List<String> type,
        Integer year,
        Integer startYear,
        Integer endYear,
        String number,
        String subject,
        String language,
        List<String> extent,
        LocalDate published,
        Integer page) {

        return isBlank(title)
            && isEmpty(type)
            && year == null
            && startYear == null
            && endYear == null
            && isBlank(number)
            && isBlank(subject)
            && isBlank(language)
            && isEmpty(extent)
            && published == null
            && page == null;
    }


}

