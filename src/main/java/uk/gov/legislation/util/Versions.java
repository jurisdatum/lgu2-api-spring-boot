package uk.gov.legislation.util;

import java.util.regex.Pattern;

public class Versions {

    private static final Pattern date = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static boolean isVersionLabel(String s) {
        if (s.equals("enacted"))
            return true;
        if (s.equals("made"))
            return true;
        if (s.equals("current"))
            return true;
        if (date.matcher(s).matches())
            return true;
        return false;
    }

}
