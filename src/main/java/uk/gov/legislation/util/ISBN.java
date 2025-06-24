package uk.gov.legislation.util;

public class ISBN {

    public static String format(String isbn) {
        if (isbn.length() == 13)
            return isbn.substring(0, 3) + "-" + format(isbn.substring(3));
        if (isbn.length() != 10)
            return isbn;
        String first = isbn.substring(0, 1);
        char second = isbn.charAt(1);
        String group, publisher;
        if (second == '0' || second == '1') {
            group     = isbn.substring(1, 3);
            publisher = isbn.substring(3, 9);
        } else if (second >= '2' && second <= '6') {
            group     = isbn.substring(1, 4);
            publisher = isbn.substring(4, 9);
        } else {
            group     = isbn.substring(1, 5);
            publisher = isbn.substring(5, 9);
        }
        String check = isbn.substring(9);
        return first + "-" + group + "-" + publisher + "-" + check;
    }

}
