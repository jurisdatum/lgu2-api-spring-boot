package uk.gov.legislation.util;

public class Roman {


    /**
     * Converts a number to a lower-case Roman numeral representation.
     *
     * @param number the number to convert, must be between 1 and 3999
     * @return the lower-case Roman numeral representation of the number
     * @throws IllegalArgumentException if the number is not within the valid range
     */
    public static String toLowerRoman(int number) {
        return toUpperRoman(number).toLowerCase();
    }

    /**
     * Converts a number to an upper-case Roman numeral representation.
     *
     * @param number the number to convert, must be between 1 and 3999
     * @return the upper-case Roman numeral representation of the number
     * @throws IllegalArgumentException if the number is not within the valid range
     */
    public static String toUpperRoman(int number) {
        validateNumberInRange(number);
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
     * Validates that the given number is within the valid range for Roman numerals.
     *
     * @param number the number to validate
     * @throws IllegalArgumentException if the number is not between 1 and 3999
     */
    private static void validateNumberInRange(int number) {
        if (number < 1 || number > 3999) {
            throw new IllegalArgumentException("The number must be between 1 and 3999.");
        }
    }

}
