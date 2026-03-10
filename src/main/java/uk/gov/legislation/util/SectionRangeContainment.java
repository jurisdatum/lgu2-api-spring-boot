package uk.gov.legislation.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Determines whether a provision identifier falls within a range defined by
 * start and end provision identifiers.
 *
 * <p>Provision identifiers are hyphen-separated tokens such as "section-1",
 * "schedule-14-paragraph-3", or "section-395-13-bc". A range like
 * "section-249-1" to "section-249-3" includes "section-249-2".
 *
 * <p>Handles hierarchical containment in both directions:
 * <ul>
 *   <li>"part-2-section-5" is within range "part-1" to "part-3" (descendant)</li>
 *   <li>"section-1" contains range "section-1-a" to "section-1-c" (ancestor)</li>
 * </ul>
 *
 * <p>Uses a top-down approach: strips matching prefix tokens, then checks
 * containment of the remaining tokens. This naturally handles the ancestor case
 * (id exhausted after prefix stripping) and the descendant case (id truncated
 * to range depth before comparison).
 *
 * <p>Ported from the legacy MarkLogic XQuery function utils:contains-section,
 * with improvements for hierarchical containment, alphabetic tokens, and
 * roman numeral handling.
 */
class SectionRangeContainment {

    /**
     * Tests whether {@code id} falls within the range [{@code start}, {@code end}] (inclusive).
     */
    static boolean contains(String start, String end, String id) {
        String[] startTokens = tokenize(start);
        String[] endTokens = tokenize(end);
        String[] idTokens = tokenize(id);

        // Strip common prefix (tokens matching in all three)
        int i = 0;
        while (i < startTokens.length && i < endTokens.length && i < idTokens.length
                && startTokens[i].equalsIgnoreCase(endTokens[i])
                && startTokens[i].equalsIgnoreCase(idTokens[i])) {
            i++;
        }

        // If id is exhausted, it's an ancestor of the range — it contains it
        if (i >= idTokens.length)
            return true;

        // Truncate id to the depth of the range boundaries, so that
        // descendants of a boundary are treated as being at that boundary
        int rangeDepth = Math.max(startTokens.length, endTokens.length);
        if (idTokens.length > rangeDepth)
            idTokens = Arrays.copyOf(idTokens, rangeDepth);

        // Check start <= id <= end using the remaining tokens
        return compareFrom(startTokens, idTokens, i) <= 0
            && compareFrom(idTokens, endTokens, i) <= 0;
    }

    /**
     * Compares two provision identifiers. A shorter ID sorts before a longer one
     * when all leading tokens match (e.g. "part-2" &lt; "part-2-section-5").
     */
    static int compare(String id1, String id2) {
        return compareFrom(tokenize(id1), tokenize(id2), 0);
    }

    private static int compareFrom(String[] a, String[] b, int from) {
        int len = Math.max(a.length, b.length);
        for (int i = from; i < len; i++) {
            if (i >= a.length) return -1;
            if (i >= b.length) return 1;
            int cmp = compareTokens(a[i], b[i]);
            if (cmp != 0) return cmp;
        }
        return 0;
    }

    private static String[] tokenize(String id) {
        return id.replace("--", "-").split("-");
    }

    /* token comparison */

    private static final Pattern NUMBER_ALPHA = Pattern.compile("^(\\d+)([a-zA-Z]*)$");
    private static final Pattern ROMAN_CHARS = Pattern.compile("^[ivxlcdm]+$", Pattern.CASE_INSENSITIVE);

    private static int compareTokens(String a, String b) {
        if (a.equalsIgnoreCase(b))
            return 0;

        // numeric, with optional alpha suffix (e.g. "1", "10", "1A", "10ZA")
        Matcher ma = NUMBER_ALPHA.matcher(a);
        Matcher mb = NUMBER_ALPHA.matcher(b);
        if (ma.matches() && mb.matches()) {
            int cmp = Integer.compare(Integer.parseInt(ma.group(1)), Integer.parseInt(mb.group(1)));
            if (cmp != 0)
                return cmp;
            return ma.group(2).compareToIgnoreCase(mb.group(2));
        }

        // roman numerals: both tokens consist of roman characters, and at least one
        // is multi-character (to avoid misclassifying single-letter paragraph labels)
        if (ROMAN_CHARS.matcher(a).matches() && ROMAN_CHARS.matcher(b).matches()
                && (a.length() > 1 || b.length() > 1)) {
            int ra = Roman.parse(a);
            int rb = Roman.parse(b);
            if (ra > 0 && rb > 0)
                return Integer.compare(ra, rb);
        }

        // alphabetic or fallback: case-insensitive lexicographic
        return a.compareToIgnoreCase(b);
    }

}
