package uk.gov.legislation.transform.clml2docx;

import uk.gov.legislation.transform.TransformHelper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class DocxTestRedo {

    public static void main(String[] args) throws Exception {
        // Create a SINGLE instance and reuse it, matching the test's behavior
        // with @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        Clml2Docx transform = new Clml2Docx(new DocxTest.Delegate());
        int updated = 0;
        for (String id : DocxTest.provide().toList()) {
            String clml = TransformHelper.read(id, "xml");
            byte[] docx = transform.transform(new ByteArrayInputStream(clml.getBytes(StandardCharsets.UTF_8)));
            String actual = DocxTest.extractDocumentXml(docx);
            String expected = TransformHelper.read(id, DocxTest.DOCX_XML_EXT);
            if (expected.equals(actual))
                continue;
            System.out.println("Updating fixture for: " + id);
            TransformHelper.write(id, DocxTest.DOCX_XML_EXT, actual);
            updated++;
        }
        System.out.println("Updated " + updated + " fixtures");
    }

}
