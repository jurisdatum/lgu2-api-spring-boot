package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.util.Extent;

import java.util.EnumSet;
import java.util.List;

public class TableOfContentsConverter {

    public static TableOfContents convert(Contents simple) {
        EnumSet<Extent> docExtent = ExtentConverter.convert(simple.meta.extent);
        TableOfContents toc = new TableOfContents();
        toc.meta = DocumentMetadataConverter.convert(simple.meta);
        if (simple.contents == null)
            return toc;
        toc.contents = new TableOfContents.Contents();
        toc.contents.title = simple.contents.title;
        if (simple.meta.hasParts.introduction != null) {
            toc.contents.introduction = new TableOfContents.Introduction();
            toc.contents.introduction.extent = docExtent;
        }
        toc.contents.body = convertItems(simple.contents.body);
        if (simple.meta.hasParts.signature != null) {
            toc.contents.signature = new TableOfContents.Signature();
            toc.contents.signature.extent = docExtent;
        }
        toc.contents.appendices = convertItems(simple.contents.appendices);
        toc.contents.attachmentsBeforeSchedules = convertItems(simple.contents.attachmentsBeforeSchedules);
        toc.contents.schedules = convertItems(simple.contents.schedules);
        toc.contents.attachments = convertItems(simple.contents.attachments);
        if (simple.meta.hasParts.note != null) {
            toc.contents.explanatoryNote = new TableOfContents.ExplanatoryNote();
            toc.contents.explanatoryNote.extent = docExtent;
        }
        if (simple.meta.hasParts.earlierOrders != null) {
            toc.contents.earlierOrders = new TableOfContents.EarlierOrders();
            toc.contents.earlierOrders.extent = docExtent;
        }
        return toc;
    }

    private static List<TableOfContents.Item> convertItems(List<Contents.Item> items) {
        if (items == null)
            return null;
        return items.stream().map(TableOfContentsConverter::convertItem).toList();
    }

    private static TableOfContents.Item convertItem(Contents.Item item) {
        TableOfContents.Item converted = new TableOfContents.Item();
        converted.name = item.name;
        converted.number = item.number;
        converted.title = item.title;
        converted.ref = item.ref;
        converted.extent = ExtentConverter.convert(item.extent);
        converted.children = convertItems(item.children);
        return converted;
    }

}
