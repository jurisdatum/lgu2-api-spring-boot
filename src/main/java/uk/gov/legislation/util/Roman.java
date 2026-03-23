package uk.gov.legislation.util;

class Roman {

    static String toLowerRoman(int number) {
        return toUpperRoman(number).toLowerCase();
    }

    static String toUpperRoman(int number) {
        if (number < 1 || number > 3999)
            throw new IllegalArgumentException("value must be between 1 and 3999");
        StringBuilder roman = new StringBuilder();
        String[] romanNumerals = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };
        int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                roman.append(romanNumerals[i]);
            }
        }
        return roman.toString();
    }

    /**
     * Parses a roman numeral string, returning its integer value,
     * or 0 if the string is not a valid roman numeral.
     * Uses roundtrip validation via {@link #toUpperRoman}.
     */
    static int parse(String s) {
        String upper = s.toUpperCase();
        int result = 0;
        int prev = 0;
        for (int i = upper.length() - 1; i >= 0; i--) {
            int value = charValue(upper.charAt(i));
            if (value == 0)
                return 0;
            if (value < prev)
                result -= value;
            else
                result += value;
            prev = value;
        }
        if (result <= 0 || result > 3999)
            return 0;
        if (!toUpperRoman(result).equals(upper))
            return 0;
        return result;
    }

    private static int charValue(char c) {
        return switch (c) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> 0;
        };
    }

}
