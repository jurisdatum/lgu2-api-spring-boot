package debug;

import net.sf.saxon.s9api.XdmNode;
import uk.gov.legislation.api.responses.DocumentMetadata;
import uk.gov.legislation.converters.DocumentMetadataConverter;
import uk.gov.legislation.transform.Akn2Html;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.Helper;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;

import java.nio.file.Files;
import java.nio.file.Path;

public class Timing {

    public static void main(String[] args) throws Exception {
        Timing instance = new Timing();
        instance.time("/debug/asp-2003-13.xml");
        instance.time("/debug/wsi-2018-191.xml");
    }

    Clml2Akn transform1;
    Akn2Html transform2;
    Simplify simplifier;

    private Timing() {
        long t1 = System.currentTimeMillis();
        transform1 = new Clml2Akn();
        long t2 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to load the Clml->Akn transform%n", (t2 - t1) / 1000.0);

        transform2 = new Akn2Html();
        long t3 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to load the AkN->HTMLtransform%n", (t3 - t2) / 1000.0);

        simplifier = new Simplify();
        long t4 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to load the metadata extractor%n", (t4 - t3) / 1000.0);
    }

    public void time(String path) throws Exception {

        System.out.println();
        System.out.println("timing " + path.substring(7));

        long t1 = System.currentTimeMillis();

        String clml = Files.readString(Path.of(Timing.class.getResource(path).toURI()));
        long t5 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to read the XML file%n", (t5 - t1) / 1000.0);

        long start = System.currentTimeMillis();

        XdmNode doc = Helper.parse(clml);
        long t6 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to parse the CLML%n", (t6 - start) / 1000.0);

        XdmNode akn = transform1.transform(doc);
        long t7 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to transform CLML to AkN; %.2f seconds total%n", (t7 - t6) / 1000.0, (t7 - start) / 1000.0);

        String html = transform2.transform(akn, false);
        long t8 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to transform AkN to HTML; %.2f seconds total%n", (t8 - t7) / 1000.0, (t8 - start) / 1000.0);

        Metadata simple = simplifier.extractDocumentMetadata(doc);
        DocumentMetadata converted = DocumentMetadataConverter.convert(simple);
        long t9 = System.currentTimeMillis();
        System.out.printf("It took %.2f seconds to extract the JSON metadata; %.2f seconds total%n", (t9 - t8) / 1000.0, (t9 - start) / 1000.0);

    }

}
