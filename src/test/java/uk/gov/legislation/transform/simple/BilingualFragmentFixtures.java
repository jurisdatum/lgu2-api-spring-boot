package uk.gov.legislation.transform.simple;

import java.io.IOException;

/**
 * Synthetic wsi/2020/1609/part/3/chapter/1 fragment with bilingual hasVersion links,
 * built by mutating the committed fragment_wsi_2024_1002_regulation_2_cy.xml fixture.
 *
 * <p>Welsh-side links (hreflang="cy") stop at 2021-02-27; English-side links (hreflang="en")
 * extend to 2022-03-28. The pair exists to exercise language-aware hasVersion filtering in
 * both directions.
 */
public final class BilingualFragmentFixtures {

    private BilingualFragmentFixtures() {}

    public static String welsh() throws IOException {
        return build(Side.WELSH, "/welsh", false);
    }

    public static String english() throws IOException {
        return build(Side.ENGLISH, "", false);
    }

    public static String welshPointInTime() throws IOException {
        return build(Side.WELSH, "/2021-05-17/welsh", true);
    }

    private enum Side { WELSH, ENGLISH }

    private static final String BILINGUAL_HAS_VERSION_LINKS = """
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/made/welsh" title="made"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2020-12-20/welsh" title="2020-12-20"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2020-12-24/welsh" title="2020-12-24"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-01-09/welsh" title="2021-01-09"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-01-15/welsh" title="2021-01-15"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-01-22/welsh" title="2021-01-22"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-01-29/welsh" title="2021-01-29"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-02-27/welsh" title="2021-02-27"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="en" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/made" title="made"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="en" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-05-17" title="2021-05-17"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="en" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2021-08-07" title="2021-08-07"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="en" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/2022-03-28" title="2022-03-28"/>
        """;

    private static final String CURRENT_HAS_VERSION_LINKS = """
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="cy" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1/welsh" title="current"/>
        <atom:link rel="http://purl.org/dc/terms/hasVersion" hreflang="en" href="http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1" title="current"/>
        """;

    private static String build(Side side, String identifierSuffix, boolean includeCurrentLinks) throws IOException {
        // Welsh translation lags English, so the Welsh-side dct:valid is earlier than the English one.
        String validDate = side == Side.WELSH ? "2021-04-26" : "2022-03-28";

        String clml = UnappliedEffectsHelper.read("/fragment_wsi_2024_1002_regulation_2_cy.xml");

        clml = requireReplace(clml,
            "title=\"HTML5 snippet\"/>",
            "title=\"HTML5 snippet\"/>\n" + BILINGUAL_HAS_VERSION_LINKS + (includeCurrentLinks ? CURRENT_HAS_VERSION_LINKS : ""));

        clml = requireReplace(clml,
            "http://www.legislation.gov.uk/wsi/2024/1002/regulation/2/made/welsh",
            "http://www.legislation.gov.uk/wsi/2020/1609/part/3/chapter/1" + identifierSuffix);

        clml = requireReplace(clml,
            "<dc:modified>2024-10-09</dc:modified><dc:subject scheme=\"SIheading\">ADDYSG, CYMRU</dc:subject>",
            "<dc:modified>2024-10-09</dc:modified><dct:valid>" + validDate + "</dct:valid><dc:subject scheme=\"SIheading\">ADDYSG, CYMRU</dc:subject>");

        clml = requireReplace(clml, "<ukm:DocumentStatus Value=\"final\"/>", "<ukm:DocumentStatus Value=\"revised\"/>");

        if (side == Side.ENGLISH) {
            clml = requireReplace(clml, "<dc:language>cy</dc:language>", "<dc:language>en</dc:language>");
        }

        return clml;
    }

    private static String requireReplace(String source, String target, String replacement) {
        if (!source.contains(target))
            throw new IllegalStateException("Base fixture no longer contains expected anchor: " + target);
        return source.replace(target, replacement);
    }
}
