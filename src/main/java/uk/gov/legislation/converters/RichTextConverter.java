package uk.gov.legislation.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.api.responses.RichText;
import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.util.Links;

import java.util.List;
import java.util.stream.Stream;

class RichTextConverter {

    private static final Logger logger = LoggerFactory.getLogger(RichTextConverter.class);

    static List<RichText.Node> convert(List<RichTextNode> clml) {
        return clml.stream().flatMap(RichTextConverter::convert1).toList();
    }

    static Stream<RichText.Node> convert1(RichTextNode clml) {
        if (clml instanceof RichTextNode.Text text)
            return Stream.of(convertText(text));
        if (clml instanceof RichTextNode.Section section)
            return Stream.of(convertSection(section));
        if (clml instanceof RichTextNode.Range range)
            return convertRange(range);
        logger.warn("unrecognized type {}", clml.getClass());
        return null;
    }

    static RichText.Node convertText(RichTextNode.Text text) {
        RichText.Node node = new RichText.Node();
        node.type = "text";
        node.text = text.text;
        return node;
    }

    static RichText.Node convertSection(RichTextNode.Section section) {
        RichText.Node node = new RichText.Node();
        node.type = "link";
        node.id = section.ref;
        node.href = Links.shorten(section.uri);
        node.missing = section.missing ? true : null;
        node.text = section.text;
        return node;
    }

    static Stream<RichText.Node> convertRange(RichTextNode.Range range) {
        return range.children.stream().flatMap(RichTextConverter::convert1);
    }

}
