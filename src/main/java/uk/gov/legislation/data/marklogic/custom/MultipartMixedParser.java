package uk.gov.legislation.data.marklogic.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Very small parser for “multipart/mixed” HTTP responses. */
public final class MultipartMixedParser {

    public static final class Part {
        public final Map<String, String> headers;
        public final String body;   // decoded as UTF-8 text

        Part(Map<String, String> headers, String body) {
            this.headers = headers;
            this.body = body;
        }
        @Override public String toString() { return body; }
    }

    /**
     * Parse an InputStream containing a multipart/mixed entity.
     *
     * @param stream   response body from HttpClient
     * @param boundary boundary token (the string after “boundary=”)
     */
    public static List<Part> parse(InputStream stream, String boundary) throws IOException {
        String delim = "--" + boundary;
        String delimClose = delim + "--";

        BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        List<Part> parts = new ArrayList<>();

        String line;

        // Skip preamble until first boundary
        while ((line = br.readLine()) != null && !line.equals(delim)) {
            if (line.equals(delimClose)) return parts; // empty multipart
        }

        while (line != null && line.equals(delim)) {
            /* ---- 1. headers --------------------------------------------------*/
            Map<String, String> headers = new LinkedHashMap<>();
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(':');
                if (idx > 0) {
                    String name = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    headers.put(name, value);
                }
            }

            /* ---- 2. body -----------------------------------------------------*/
            StringBuilder body = new StringBuilder();
            while ((line = br.readLine()) != null &&
                !line.equals(delim) && !line.equals(delimClose)) {
                body.append(line).append('\n');
            }

            // Remove the final newline we added
            if (body.length() > 0) {
                body.setLength(body.length() - 1);
            }

            parts.add(new Part(headers, body.toString()));

            // line is now either null, delim, or delimClose
            if (line == null || line.equals(delimClose)) {
                break;
            }
            // if line.equals(delim), continue to next iteration
        }

        return parts;
    }

    private MultipartMixedParser() {}  // static utility
}
