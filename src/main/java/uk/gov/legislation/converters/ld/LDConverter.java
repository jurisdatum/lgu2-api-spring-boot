package uk.gov.legislation.converters.ld;

import java.net.URI;
import java.time.LocalDate;

class LDConverter {

    static String extractLastComponentOfUri(URI uri) {
        if (uri == null)
            return null;
        String s = uri.toASCIIString();
        return s.substring(s.lastIndexOf('/') + 1);
    }

    static Integer extractIntegerAtEndOfUri(URI uri) {
        String num = extractLastComponentOfUri(uri);
        if (num == null)
            return null;
        return Integer.parseInt(num);
    }

    static LocalDate extractDateAtEndOfUri(URI uri) {
        String date = extractLastComponentOfUri(uri);
        if (date == null)
            return null;
        return LocalDate.parse(date);
    }

}
