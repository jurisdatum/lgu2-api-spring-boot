package uk.gov.legislation.converters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.legislation.endpoints.document.responses.RichText;
import uk.gov.legislation.transform.simple.UnappliedEffect;
import uk.gov.legislation.util.Links;

class RichTextConverter {

    private static final Logger logger = LoggerFactory.getLogger(RichTextConverter.class);

    static RichText.Node convert(UnappliedEffect.RichTextNode clml) {
        RichText.Node node = new RichText.Node();
        node.text = clml.text;
        switch (clml.type) {
            case null -> logger.warn("node type is null");
            case UnappliedEffect.RichTextNode.TEXT_TYPE -> node.type = "text";
            case UnappliedEffect.RichTextNode.SECTION_TYPE -> {
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
