package uk.gov.legislation.util;

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
        if (s.equals("current"))
            return true;
        if (s.equals("prospective"))
            return true;
        if (date.matcher(s).matches())
            return true;
        return false;
    }

//    public static Optional<LocalDate> getLastDate(List<String> versions) {
//        return versions.stream()
//                .filter(Versions::isDate)
//                .reduce((first, second) -> second)
//                .map(v -> LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE));
//    }
//
//    public static boolean isDate(String version) {
//        try {
//            LocalDate.parse(version, DateTimeFormatter.ISO_LOCAL_DATE);
//            return true;
//        } catch (DateTimeParseException e) {
//            return false;
//        }
//    }

}
