package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class RegnalYear {

    String[] parts;

    private RegnalYear(String[] parts) {
        this.parts = parts;
    }

    public static RegnalYear parse(String value) {
        String[] parts = value.split("[/_]");
        if (parts.length < 2)
            throw new IllegalArgumentException(value);
        return new RegnalYear(parts);
    }

    public static Optional<RegnalYear> extractFromId(String id) {
        String[] parts = id.split("[/_]");
        if (parts.length < 4)
            return Optional.empty();
        String[] middle = Arrays.copyOfRange(parts, 1, parts.length - 1);
        return Optional.of(new RegnalYear(middle));
    }

    @Override
    public String toString() {
        return String.join("/", parts);
    }

    public String forCitation() {
        String[] fixed = combineYears(parts);
        addPunctuation(fixed);
        return "(" + String.join(" ", fixed) + ")";
    }

    static String forCitation(RegnalYear regnal) {
        if (regnal == null)
            return "";
        return " " + regnal.forCitation();
    }

    private static boolean isAllDigits(String s) {
        return s.chars().allMatch(Character::isDigit);  // !s.isEmpty() &&
    }

    public static String[] combineYears(String[] input) {
        for (int i = 0; i < input.length - 2; i++) {
            if (isAllDigits(input[i]) && "and".equals(input[i + 1]) && isAllDigits(input[i + 2])) {
                String replacement = input[i] + " & " + input[i + 2];
                String[] result = new String[input.length - 2];
                System.arraycopy(input, 0, result, 0, i);
                result[i] = replacement;
                System.arraycopy(input, i + 3, result, i + 1, input.length - (i + 3));
                return result;
            }
        }
        return input;
    }

    private static final Pattern MONARCH = Pattern.compile("[A-Z][a-z]+");

    public static void addPunctuation(String[] input) {
        for (int i = 0; i < input.length; i++)
            if (MONARCH.matcher(input[i]).matches())
                input[i] = input[i] + ".";
    }

}
