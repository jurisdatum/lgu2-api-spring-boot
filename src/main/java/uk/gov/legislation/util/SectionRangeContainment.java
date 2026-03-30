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

    // optional alpha prefix + digits + optional alphanumeric suffix
    // e.g. "1", "1A", "10ZA", "360Z10", "ZA1", "A1", "B1"
    private static final Pattern PROVISION_NUMBER = Pattern.compile("^([a-zA-Z]*)(\\d+)([a-zA-Z0-9]*)$");
    private static final Pattern ROMAN_CHARS = Pattern.compile("^[ivxlcdm]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern ALPHA_ONLY = Pattern.compile("^[a-zA-Z]+$");

    private static int compareTokens(String a, String b) {
        if (a.equalsIgnoreCase(b))
            return 0;

        // provision numbers: prefix < bare < suffix (ZA1 < A1 < 1 < 1ZA < 1A)
        Matcher ma = PROVISION_NUMBER.matcher(a);
        Matcher mb = PROVISION_NUMBER.matcher(b);
        if (ma.matches() && mb.matches()) {
            int cmp = Integer.compare(Integer.parseInt(ma.group(2)), Integer.parseInt(mb.group(2)));
            if (cmp != 0)
                return cmp;
            // category: has-prefix (0) < bare (1) < has-suffix (2)
            int catA = !ma.group(1).isEmpty() ? 0 : ma.group(3).isEmpty() ? 1 : 2;
            int catB = !mb.group(1).isEmpty() ? 0 : mb.group(3).isEmpty() ? 1 : 2;
            if (catA != catB)
                return Integer.compare(catA, catB);
            if (catA == 0)
                return compareSuffix(ma.group(1), mb.group(1));
            if (catA == 2)
                return compareSuffix(ma.group(3), mb.group(3));
            return 0;
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

        // alphabetic (paragraph labels, etc.) using insertion ordering
        if (ALPHA_ONLY.matcher(a).matches() && ALPHA_ONLY.matcher(b).matches())
            return compareSuffix(a, b);

        // fallback: case-insensitive lexicographic
        return a.compareToIgnoreCase(b);
    }

    /* suffix comparison for inserted provisions */

    /**
     * Compares two provision suffixes according to UK legislation insertion conventions.
     * <p>The ordering follows the guidance in section 6.4 of Statutory Instrument Practice:
     * <ul>
     *   <li>Z followed by a letter is a prefix meaning "before" (ZA, ZB... sort before A)</li>
     *   <li>Letters A through Y are ordered normally</li>
     *   <li>Z at the end (or followed by a digit) is the letter Z (sorts after Y)</li>
     *   <li>The convention is recursive: ZZA sorts before ZA, which sorts before A</li>
     * </ul>
     * <p>Example ordering: ZZA &lt; ZA &lt; ZB &lt; A &lt; AZA &lt; AA &lt; AB &lt; AZ &lt; B &lt; ... &lt; Z
     */
    static int compareSuffix(String s1, String s2) {
        if (s1.equalsIgnoreCase(s2))
            return 0;
        if (s1.isEmpty())
            return -1;
        if (s2.isEmpty())
            return 1;

        // Numeric sub-suffixes (e.g. "10" in "Z10"): compare numerically
        if (Character.isDigit(s1.charAt(0)) && Character.isDigit(s2.charAt(0))) {
            int i1 = 0;
            while (i1 < s1.length() && Character.isDigit(s1.charAt(i1))) i1++;
            int i2 = 0;
            while (i2 < s2.length() && Character.isDigit(s2.charAt(i2))) i2++;
            int cmp = Integer.compare(
                Integer.parseInt(s1.substring(0, i1)),
                Integer.parseInt(s2.substring(0, i2)));
            if (cmp != 0)
                return cmp;
            return compareSuffix(s1.substring(i1), s2.substring(i2));
        }

        int rank1 = suffixRank(s1);
        int rank2 = suffixRank(s2);
        if (rank1 != rank2)
            return Integer.compare(rank1, rank2);

        // Same rank: strip first character and recurse
        return compareSuffix(s1.substring(1), s2.substring(1));
    }

    /**
     * Returns the sort rank of the first character of a suffix string.
     * Z followed by a letter = 0 (before everything).
     * A through Y = 1 through 25.
     * Z at end or followed by a digit = 26 (after Y).
     */
    private static int suffixRank(String s) {
        char first = Character.toUpperCase(s.charAt(0));
        if (first == 'Z' && s.length() > 1 && Character.isLetter(s.charAt(1)))
            return 0;
        return first - 'A' + 1;
    }

}
