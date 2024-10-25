package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;
import uk.gov.legislation.api.document.Metadata;
import uk.gov.legislation.api.documents.DocumentList;
import uk.gov.legislation.util.Links;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AkN {

    private static final XPathExecutable workUri;
    private static final XPathExecutable exprUri;
    private static final XPathExecutable longType;
    private static final XPathExecutable shortType;
    private static final XPathExecutable year;
    private static final XPathExecutable number;
    private static final XPathExecutable altNumbers;
    private static final XPathExecutable date;
    private static final XPathExecutable cite;
    private static final XPathExecutable status;
    private static final XPathExecutable title;
    private static final XPathExecutable lang;
    private static final XPathExecutable publisher;
    private static final XPathExecutable modified;
    private static final XPathExecutable versions;
    private static final XPathExecutable schedules;
    private static final XPathExecutable xmlFormat;
    private static final XPathExecutable alternatives;
    private static final XPathExecutable dcIdentifier;
    private static final XPathExecutable prevLink;
    private static final XPathExecutable nextLink;

    static {
        XPathCompiler compiler = Helper.processor.newXPathCompiler();
        compiler.declareNamespace("", "http://docs.oasis-open.org/legaldocml/ns/akn/3.0");
        compiler.declareNamespace("dc", "http://purl.org/dc/elements/1.1/");
        compiler.declareNamespace("ukm", "http://www.legislation.gov.uk/namespaces/metadata");
        compiler.declareNamespace("atom", "http://www.w3.org/2005/Atom");
        try {
            workUri = compiler.compile("/akomaNtoso/*/meta/identification/FRBRWork/FRBRthis/@value");
            exprUri = compiler.compile("/akomaNtoso/*/meta/identification/FRBRExpression/FRBRthis/@value");
            longType = compiler.compile("/akomaNtoso/*/meta/proprietary/ukm:*/ukm:DocumentClassification/ukm:DocumentMainType/@Value");
            shortType = compiler.compile("/akomaNtoso/*/@name");
            year = compiler.compile("/*/*/meta/proprietary/ukm:*/ukm:Year/@Value");
            number = compiler.compile("/*/*/meta/identification/FRBRWork/FRBRnumber[1]/@value");
            altNumbers = compiler.compile("/*/*/meta/identification/FRBRWork/FRBRnumber/@value[matches(., '^[A-Z]+\\. \\d+$')]");
            date = compiler.compile("/*/*/meta/identification/FRBRWork/FRBRdate/@date");
            cite = compiler.compile("/akomaNtoso/*/meta/identification/FRBRWork/FRBRname/@value");
            status = compiler.compile("/akomaNtoso/*/meta/proprietary/ukm:*/ukm:DocumentClassification/ukm:DocumentStatus/@Value");
            title = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:title");
            lang = compiler.compile("/akomaNtoso/*/meta/identification/FRBRExpression/FRBRlanguage/@language");
            publisher = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:publisher");
            modified = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:modified");
            versions = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='http://purl.org/dc/terms/hasVersion']/@title");
            schedules = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='http://www.legislation.gov.uk/def/navigation/schedules']/@href");
            xmlFormat = compiler.compile("/*/*/*[not(self::meta)]");
            alternatives = compiler.compile("/*/*/meta/proprietary/ukm:Alternatives/ukm:Alternative");
            dcIdentifier = compiler.compile("/*/*/meta/proprietary/dc:identifier");
            prevLink = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='prev']/@href");
            nextLink = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='next']/@href");
        } catch (SaxonApiException e) {
            throw new RuntimeException("error compiling xpath expression", e);
        }
    }

    private static String get(XPathExecutable exec, XdmNode akn) {
        XPathSelector selector = exec.load();
        try {
            selector.setContextItem(akn);
        } catch (SaxonApiException e) {
            throw new RuntimeException("error setting context item", e);
        }
        XdmItem result;
        try {
            result = selector.evaluateSingle();
        } catch (SaxonApiException e) {
            throw new RuntimeException("error evaluating xpath expression", e);
        }
        if (result == null)
            return null;
        return result.getStringValue();
    }

    private static XdmValue evaluate(XPathExecutable exec, XdmNode akn) {
        XPathSelector selector = exec.load();
        try {
            selector.setContextItem(akn);
        } catch (SaxonApiException e) {
            throw new RuntimeException("error setting context item", e);
        }
        XdmValue result;
        try {
            result = selector.evaluate();
        } catch (SaxonApiException e) {
            throw new RuntimeException("error evaluating xpath expression", e);
        }
        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private static List<String> getMany(XPathExecutable exec, XdmNode akn) {
        XdmValue result = evaluate(exec, akn);
        if (result == null)
            return null;
        return result.stream().map(XdmItem::getStringValue).toList();
    }

    public static String getId(XdmNode akn) {
        String longId = get(workUri, akn);
        if (longId == null)
            return null;
        return longId.substring(33);
    }

    public static String getVersion(XdmNode akn, String id) {
        String uri = get(exprUri, akn);
        if (uri == null)
            return null;
        int start = 31 + id.length();
        return uri.substring(start);
    }

    public static String getLongType(XdmNode akn) { return get(longType, akn); }

    public static String getShortType(XdmNode akn) { return get(shortType, akn); }

    public static String getRegnalYear(String id) {
        String[] components = id.split("/");
        if (components.length == 3)
            return null;
        return Arrays.stream(components, 1, components.length - 1).collect(Collectors.joining("/"));
    }

    public static int getYear(XdmNode akn) {
        String str = get(year, akn);
        if (str == null)
            return 0;
        return Integer.parseInt(str);
    }

    public static int getNumber(XdmNode akn) {
        String str = get(number, akn);
        if (str == null)
            return 0;
        return Integer.parseInt(str);
    }

    private record AltNumber(String category, String value) implements DocumentList.Document.AltNumber { }

    private static List<AltNumber> getAltNumbers(XdmNode akn) {
        List<String> values = getMany(altNumbers, akn);
        if (values == null)
            return null;
        Stream<AltNumber> stream = values.stream().map(value -> {
            String[] parts = value.split("\\.", 2);
            return new AltNumber(parts[0], parts[1].trim());
        });
        return stream.toList();
    }

    public static LocalDate getDate(XdmNode akn) {
        String str = get(date, akn);
        if (str == null)
            return null;
        return LocalDate.parse(str);
    }

    public static String getCite(XdmNode akn) { return get(cite, akn); }

    public static String getStatus(XdmNode akn) { return get(status, akn); }

    public static String getTitle(XdmNode akn) {
        return get(title, akn);
    }

    public static String getLang(XdmNode akn) {
        return get(lang, akn);
    }

    public static String getPublisher(XdmNode akn) {
        return get(publisher, akn);
    }

    public static LocalDate getModified(XdmNode akn) {
        String str = get(modified, akn);
        if (str == null)
            return null;
        return LocalDate.parse(str);
    }

    public static List<String> getVersions(XdmNode akn, String current) {
        XdmValue result = evaluate(versions, akn);
        LinkedHashSet<String> versions = StreamSupport.stream(result.spliterator(), false)
            .map(XdmItem::getStringValue)
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (versions.contains("current")) {
            versions.remove("current");
            versions.add(current);
        }
        versions.remove("prospective"); // FixMe ?!
        return versions.stream().toList();
    }

    public static boolean hasSchedules(XdmNode akn) {
        String link = get(schedules, akn);
        return link != null;
    }

    public record Format1(String name, String uri) implements uk.gov.legislation.api.document.Metadata.Format { }

    public static List<Format1> getFormats(XdmNode akn) {
        List<Format1> formats = new ArrayList<>(2);
        if (!evaluate(xmlFormat, akn).isEmpty()) {
            Format1 xml = new Format1("xml", null);
            formats.add(xml);
        }
        evaluate(alternatives, akn).stream().forEach(i -> {
            XdmNode e = (XdmNode) i;
            final String uri = e.attribute("URI");
            if (!uri.endsWith(".pdf"))
                return;
            if ("Welsh".equals(e.attribute("Language")))  // ToDo
                return;
            Format1 pdf = new Format1("pdf", uri);
            formats.add(pdf);
        });
        return formats;
    }

    public static String getFragmentIdentifier(XdmNode akn) {
        String link = get(dcIdentifier, akn);
        return Links.extractFragmentIdentifierFromLink(link);
    }

    public static String getPreviousLink(XdmNode akn) {
        String link = get(prevLink, akn);
        return Links.extractFragmentIdentifierFromLink(link);
    }
    public static String getNextLink(XdmNode akn) {
        String link = get(nextLink, akn);
        return Links.extractFragmentIdentifierFromLink(link);
    }

public record Meta(
        String id,
        String longType,
        String shortType,
        int year,
        String regnalYear,
        int number,
        List<? extends DocumentList.Document.AltNumber> altNumbers,
        LocalDate date,
        String cite,
        String version,
        String status,
        String title,
        String lang,
        String publisher,
        LocalDate modified,
        List<String> versions,
        boolean schedules,
        List<Format1> formats,
        String fragment,
        String prev,
        String next

) implements Metadata {

    public static Meta extract(XdmNode akn) {
        String id = AkN.getId(akn);
        String longType = AkN.getLongType(akn);
        String shortType = AkN.getShortType(akn);
        String regnalYear = id == null ? null : AkN.getRegnalYear(id);
        int year = AkN.getYear(akn);
        int number = AkN.getNumber(akn);
        List<AltNumber> altNumbers = AkN.getAltNumbers(akn);
        LocalDate date = AkN.getDate(akn);
        String cite = AkN.getCite(akn);
        String status = AkN.getStatus(akn);
        String version = AkN.getVersion(akn, id);
        String title = AkN.getTitle(akn);
        String lang = AkN.getLang(akn);
        String publisher = AkN.getPublisher(akn);
        LocalDate modified = AkN.getModified(akn);
        List<String> versions = AkN.getVersions(akn, version);
        boolean schedules = AkN.hasSchedules(akn);
        List<Format1> formats = AkN.getFormats(akn);
        String fragment = AkN.getFragmentIdentifier(akn);
        String prev = AkN.getPreviousLink(akn);
        String next = AkN.getNextLink(akn);
        return new Meta(id, longType, shortType, year, regnalYear, number, altNumbers, date, cite,
            version, status, title, lang, publisher, modified, versions, schedules, formats,
            fragment, prev, next);
    }

}

}
