package uk.gov.legislation.transform;

import net.sf.saxon.s9api.*;

public class AkN {

    private static XPathExecutable exec;
    static {
        XPathCompiler compiler = Helper.processor.newXPathCompiler();
        compiler.declareNamespace("", "http://docs.oasis-open.org/legaldocml/ns/akn/3.0");
        compiler.declareNamespace("dc", "http://purl.org/dc/elements/1.1/");
        try {
            exec = compiler.compile("/akomaNtoso/*/meta/proprietary/dc:title");
        } catch (SaxonApiException e) {
            throw new RuntimeException("error compiling xpath expression", e);
        }
    }

    public static String getTitle(XdmNode akn) {
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
        return result.getStringValue();
    }

}
