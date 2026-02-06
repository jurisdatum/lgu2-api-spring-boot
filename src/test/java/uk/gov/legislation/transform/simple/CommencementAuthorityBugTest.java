package uk.gov.legislation.transform.simple;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommencementAuthorityBugTest {

    @Test
    void preservesRangeDelimiter() throws Exception {
        String xml = """
            <Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation"
                         xmlns:ukm="http://www.legislation.gov.uk/namespaces/metadata"
                         xmlns:dc="http://purl.org/dc/elements/1.1/">
              <ukm:Metadata>
                <dc:identifier>http://www.legislation.gov.uk/test/123</dc:identifier>
                <ukm:PrimaryMetadata>
                  <ukm:UnappliedEffects>
                    <ukm:UnappliedEffect>
                      <ukm:CommencementAuthority>
                        <ukm:SectionRange Start="s1" End="s2" URI="rangeUri">
                          <ukm:Section Ref="s1" URI="uri1">s. 1</ukm:Section>
                          -
                          <ukm:Section Ref="s2" URI="uri2">s. 2</ukm:Section>
                        </ukm:SectionRange>
                      </ukm:CommencementAuthority>
                    </ukm:UnappliedEffect>
                  </ukm:UnappliedEffects>
                </ukm:PrimaryMetadata>
              </ukm:Metadata>
            </Legislation>
            """;

        Simplify simplify = new Simplify();
        Metadata metadata = simplify.extractDocumentMetadata(xml);

        assertNotNull(metadata.rawEffects);
        assertEquals(1, metadata.rawEffects.size());
        var effect = metadata.rawEffects.get(0);
        assertNotNull(effect.commencementAuthority);
        assertEquals(1, effect.commencementAuthority.size());

        RichTextNode rangeNode = effect.commencementAuthority.get(0);
        assertInstanceOf(RichTextNode.Range.class, rangeNode);
        RichTextNode.Range range = (RichTextNode.Range) rangeNode;
        assertNotNull(range.children);

        var sections = range.children.stream()
            .filter(RichTextNode.Section.class::isInstance)
            .map(RichTextNode.Section.class::cast)
            .toList();
        assertEquals(2, sections.size());
        assertEquals("s1", sections.get(0).ref);
        assertEquals("s. 1", sections.get(0).text);
        assertEquals("s2", sections.get(1).ref);
        assertEquals("s. 2", sections.get(1).text);

        var textTokens = range.children.stream()
            .filter(RichTextNode.Text.class::isInstance)
            .map(RichTextNode.Text.class::cast)
            .map(text -> text.text == null ? "" : text.text.trim())
            .filter(token -> !token.isEmpty())
            .toList();
        assertTrue(textTokens.contains("-"));
    }
}
