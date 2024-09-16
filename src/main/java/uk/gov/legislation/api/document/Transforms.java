package uk.gov.legislation.api.document;

import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.simple.Simplify;

public class Transforms {

    private static Clml2Akn clml2Akn = null;

    static Clml2Akn clml2akn() {
        if (clml2Akn == null)
            clml2Akn = new Clml2Akn();
        return clml2Akn;
    }

    private static Akn2Html akn2html = null;

    static Akn2Html akn2html() {
        if (akn2html == null)
            akn2html = new Akn2Html();
        return akn2html;
    }

    private static Simplify simplify = null;

    static Simplify simplifier() {
        if (simplify == null)
            simplify = new Simplify();
        return simplify;
    }

}
