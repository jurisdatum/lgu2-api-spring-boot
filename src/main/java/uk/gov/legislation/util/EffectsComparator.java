package uk.gov.legislation.util;

import uk.gov.legislation.transform.simple.UnappliedEffect;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EffectsComparator {

    private static final Comparator<UnappliedEffect> BY_AFFECTED_PROVISION = (UnappliedEffect e1, UnappliedEffect e2) -> {
        Optional<String> id1 = e1.affectedProvisions.stream()
            .filter(node -> node.type.equals(UnappliedEffect.RichTextNode.SECTION_TYPE))
            .map(node -> node.ref)
            .findFirst();
        Optional<String> id2 = e2.affectedProvisions.stream()
            .filter(node -> node.type.equals(UnappliedEffect.RichTextNode.SECTION_TYPE))
            .map(node -> node.ref)
            .findFirst();
        if (id1.isEmpty() && id2.isEmpty())
            return 0;
        if (id1.isEmpty())
            return -1;
        if (id2.isEmpty())
            return 1;
        return compareFragmentIds(id1.get(), id2.get());
    };

    static int compareFragmentIds(String id1, String id2) {
        List<String> tokens1 = Arrays.asList(id1.replace("--", "-").split("-"));
        List<String> tokens2 = Arrays.asList(id2.replace("--", "-").split("-"));
        return Comparator
            .comparing((List<String> tokens) -> tsoSortOrder(tokens, 1))
            .thenComparing((List<String> tokens) -> tsoSortOrder(tokens, 2))
            .thenComparing((List<String> tokens) -> tsoSortOrder(tokens, 3))
            .thenComparing((List<String> tokens) -> tsoSortOrder(tokens, 4))
            .thenComparing((List<String> tokens) -> tsoSortOrder(tokens, 5))
            .compare(tokens1, tokens2);
    }

    static final Comparator<UnappliedEffect> INSTANCE = BY_AFFECTED_PROVISION
        .thenComparing((UnappliedEffect e) -> e.type)
        .thenComparing((UnappliedEffect e) -> e.affectingURI);


    /* from unapplied_effects_xhtml_core.xsl */

    private static long tsoSortOrder(List<String> tokens, int item) {
        if (tokens.size() < item)
            return 0;
        String token = tokens.get(item - 1);
        if (token.contains("title") || token.contains("cross"))
            return 0;
        if (token.equalsIgnoreCase("act"))
            return 5;
        if (token.equals("part") || token.equals("Pt."))
            return 10;
        if (token.equals("chapter") || token.equals("Ch."))
            return 15;
        if (token.equals("section") || token.equals("s."))
            return 20;
        if (token.equals("schedule") || token.equals("Sch."))
            return 30;
        if (token.equals("paragraph"))
            return 40;
        Matcher matcher = Pattern.compile("^\\d+$").matcher(token);
        if (matcher.find()) {
            long num = Long.parseLong(matcher.group());
            return 3000000000L + num * 1000000L;
        }
        matcher = Pattern.compile("^(\\d+)([A-Z])$").matcher(token);
        if (matcher.find()) {
            long num = Long.parseLong(matcher.group(1));
            long letter = matcher.group(2).charAt(0);
            return 3000000000L + num * 1000000L + letter;
        }
        matcher = Pattern.compile("^(\\d+)([A-Z]{2})$").matcher(token);
        if (matcher.find()) {
            long num = Long.parseLong(matcher.group(1));
            long letter = ((long) matcher.group(2).charAt(0)) + ((long) matcher.group(2).charAt(1));
            return 3000000000L + num * 1000000L + letter;
        }
        // ToDo roman
        matcher = Pattern.compile("^[a-z]$").matcher(token);
        if (matcher.find()) {
            long letter = matcher.group().charAt(0);
            return 50000000 + letter * 1000;
        }
        return 0;
    }

}
