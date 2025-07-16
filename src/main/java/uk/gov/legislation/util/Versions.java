package uk.gov.legislation.util;

import java.util.Comparator;
import java.util.regex.Pattern;

public class Versions {

    private static final Pattern date = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static boolean isVersionLabel(String s) {
        if (s.equals("enacted"))
            return true;
        if (s.equals("made"))
            return true;
        if (s.equals("created"))
            return true;
        if (s.equals("adopted"))
            return true;
        if (s.equals("current"))
            return true;
        if (s.equals("prospective"))
            return true;
        return date.matcher(s).matches();
    }

    private static int rank(String s) {
        return switch (s) {
            case "enacted", "made", "created", "adopted" -> 0;
            case "prospective" -> 2;
            default -> 1;
        };
    }

    public static final Comparator<String> COMPARATOR = Comparator
        .comparingInt(Versions::rank)
        .thenComparing(Comparator.naturalOrder());

}
