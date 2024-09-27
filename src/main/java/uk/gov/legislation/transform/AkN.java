package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;
import uk.gov.legislation.api.document.Metadata;
import uk.gov.legislation.util.Links;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AkN {

    private static XPathExecutable workUri;
    private static XPathExecutable exprUri;
    private static XPathExecutable longType;
    private static XPathExecutable shortType;
    private static XPathExecutable year;
    private static XPathExecutable number;
    private static XPathExecutable date;
    private static XPathExecutable cite;
    private static XPathExecutable status;
    private static XPathExecutable title;
    private static XPathExecutable lang;
    private static XPathExecutable publisher;
    private static XPathExecutable modified;
    private static XPathExecutable versions;
    private static XPathExecutable dcIdentifier;
    private static XPathExecutable prevLink;
    private static XPathExecutable nextLink;
    private static XPathExecutable schedules;

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
            number = compiler.compile("/*/*/meta/identification/FRBRWork/FRBRnumber/@value");
            date = compiler.compile("/*/*/meta/identification/FRBRWork/FRBRdate/@date");
            cite = compiler.compile("/akomaNtoso/*/meta/identification/FRBRWork/FRBRname/@value");
            status = compiler.compile("/akomaNtoso/*/meta/proprietary/ukm:*/ukm:DocumentClassification/ukm:DocumentStatus/@Value");
            title = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:title");
            lang = compiler.compile("/akomaNtoso/*/meta/identification/FRBRExpression/FRBRlanguage/@language");
            publisher = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:publisher");
            modified = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:modified");
            versions = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='http://purl.org/dc/terms/hasVersion']/@title");
            dcIdentifier = compiler.compile("/*/*/meta/proprietary/dc:identifier");
            prevLink = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='prev']/@href");
            nextLink = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='next']/@href");
            schedules = compiler.compile("/*/*/meta/proprietary/atom:link[@rel='http://www.legislation.gov.uk/def/navigation/schedules']/@href");
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

    public static String getId(XdmNode akn) {
        String longId = get(workUri, akn);
        return longId.substring(33);
    }

    public static String getVersion(XdmNode akn, String id) {
        String uri = get(exprUri, akn);
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
        return Integer.parseInt(str);
    }

    public static int getNumber(XdmNode akn) {
        String str = get(number, akn);
        return Integer.parseInt(str);
    }

    public static LocalDate getDate(XdmNode akn) {
        String str = get(date, akn);
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
        return LocalDate.parse(str);
    }

    public static List<String> getVersions(XdmNode akn, String current) {
        XPathSelector selector = versions.load();
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
        LinkedHashSet<String> versions = StreamSupport.stream(result.spliterator(), false)
            .map(item -> item.getStringValue())
            .collect(Collectors.toCollection(LinkedHashSet::new));
        if (versions.contains("current")) {
            versions.remove("current");
            versions.add(current);
        }
        versions.remove("prospective"); // ?!
        return versions.stream().toList();
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

    public static boolean hasSchedules(XdmNode akn) {
        String link = get(schedules, akn);
        return link != null;
    }

public static record Meta(
        String id,
        String longType,
        String shortType,
        int year,
        String regnalYear,
        int number,
        LocalDate date,
        String cite,
        String version,
        String status,
        String title,
        String lang,
        String publisher,
        LocalDate modified,
        List<String> versions,
        String fragment,
        String prev,
        String next,
        boolean schedules

) implements Metadata {

    public static Meta extract(XdmNode akn) {
        String id = AkN.getId(akn);
        String longType = AkN.getLongType(akn);
        String shortType = AkN.getShortType(akn);
        String regnalYear = AkN.getRegnalYear(id);
        int year = AkN.getYear(akn);
        int number = AkN.getNumber(akn);
        LocalDate date = AkN.getDate(akn);
        String cite = AkN.getCite(akn);
        String status = AkN.getStatus(akn);
        String version = AkN.getVersion(akn, id);
        String title = AkN.getTitle(akn);
        String lang = AkN.getLang(akn);
        String publisher = AkN.getPublisher(akn);
        LocalDate modified = AkN.getModified(akn);
        List<String> versions = AkN.getVersions(akn, version);
        String fragment = AkN.getFragmentIdentifier(akn);
        String prev = AkN.getPreviousLink(akn);
        String next = AkN.getNextLink(akn);
        boolean schedules = AkN.hasSchedules(akn);
        Meta meta = new Meta(id, longType, shortType, year, regnalYear, number, date, cite,
            version, status, title, lang, publisher, modified, versions,
            fragment, prev, next, schedules);
        return meta;
    }

}

}
