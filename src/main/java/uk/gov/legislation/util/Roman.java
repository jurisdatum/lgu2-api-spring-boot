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

}
