package uk.gov.legislation.util;

import java.util.Set;
import java.util.regex.Pattern;

public class Versions {

    private static final Pattern date = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    private static final Set <String> VERSION_LABELS = Set.of("enacted", "made", "created", "current", "prospective");

    public static boolean isVersionLabel(String s) {
        return VERSION_LABELS.contains(s) || date.matcher(s).matches();
    }
}
