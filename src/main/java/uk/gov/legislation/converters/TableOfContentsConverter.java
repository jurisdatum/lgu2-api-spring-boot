package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.TableOfContents;
import uk.gov.legislation.transform.simple.Contents;

import java.util.List;

public class TableOfContentsConverter {

    public static TableOfContents convert(Contents simple) {
        TableOfContents toc = new TableOfContents();
        toc.meta = DocumentMetadataConverter.convert(simple.meta);
        if (simple.contents == null)
            return toc;
        toc.contents = new TableOfContents.Contents();
        toc.contents.title = simple.contents.title;
        toc.contents.body = convertItems(simple.contents.body);
        toc.contents.appendices = convertItems(simple.contents.appendices);
        toc.contents.attachmentsBeforeSchedules = convertItems(simple.contents.attachmentsBeforeSchedules);
        toc.contents.schedules = convertItems(simple.contents.schedules);
        toc.contents.attachments = convertItems(simple.contents.attachments);
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
