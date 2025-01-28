package uk.gov.legislation.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.api.responses.RichText;
import uk.gov.legislation.transform.simple.RichTextNode;
import uk.gov.legislation.util.Links;

class RichTextConverter {

    private static final Logger logger = LoggerFactory.getLogger(RichTextConverter.class);

    static RichText.Node convert(RichTextNode clml) {
        RichText.Node node = new RichText.Node();
        node.text = clml.text;
        switch (clml.type) {
            case null -> logger.warn("node type is null");
            case RichTextNode.TEXT_TYPE -> node.type = "text";
            case RichTextNode.SECTION_TYPE -> {
                node.type = "link";
                node.id = clml.ref;
                node.href = Links.shorten(clml.uri);
                node.missing = clml.missing ? true : null;
            }
            default -> logger.warn("unrecognized node type: {}", clml.type);
        }
        return node;
    }

}
