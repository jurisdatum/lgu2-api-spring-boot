package uk.gov.legislation.converters;

import uk.gov.legislation.api.responses.meta.AltFormat;
import uk.gov.legislation.transform.simple.Alternative;

import java.util.List;

public class AlternateFormatConverter {

    static List<AltFormat> convert(List<Alternative> alts) {
        if (alts == null)
            return List.of();
        return alts.stream().map(AlternateFormatConverter::convert).toList();
    }

    public static AltFormat convert(Alternative clml) {
        AltFormat doc = new AltFormat();
        doc.url = clml.uri;
        doc.label = clml.title;
        if (doc.label == null)
            doc.label = clml.welshTitle;
        if (doc.label == null && Boolean.TRUE.equals(clml.print))
            doc.label = "Original: King's Printer Version";
        doc.date = clml.date;
        doc.size = clml.size == null ? null : clml.size.longValue();
        doc.language = clml.language;
        if (doc.language == null)
            doc.language = "English";
        String urlString = doc.url.toString();
        if (urlString.contains("/pdfs/")) {
            doc.type = "application/pdf";
            doc.thumbnail = makeThumbnailUrl(urlString);
        }
        return doc;
    }

    private static String makeThumbnailUrl(String pdfUrl) {
        return pdfUrl
            .replaceFirst("/pdfs/", "/images/")
            .replaceFirst("\\.pdf", ".jpg");
    }

}
