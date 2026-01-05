package uk.gov.legislation.transform;

import net.sf.saxon.s9api.SaxonApiException;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;
import uk.gov.legislation.transform.clml2docx.DocxTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

class DocxTestRedo {

    public static void main(String[] args) throws Exception {
        DocxTestRedo instance = new DocxTestRedo();
        for (String id: DocxTest.provide().toList())
            instance.redo1(id);
    }

    private final Clml2Docx transform = new Clml2Docx(new DocxTest.Delegate());

    public DocxTestRedo() throws IOException { }

    private void redo1(String id) throws IOException, SaxonApiException {
        String clml = TransformHelper.read(id, "xml");
        String expected;
        try {
            expected = TransformHelper.read(id, DocxTest.DOCX_XML_EXT);
        } catch (NullPointerException e) {
            expected = ZonedDateTime.now().toString();
        }
        byte[] docx = transform.transform(new ByteArrayInputStream(clml.getBytes(StandardCharsets.UTF_8)));
        String actual = DocxTest.extractDocumentXml(docx);
        if (actual.equals(expected))
            return;
        System.out.println("redoing " + id);
        TransformHelper.write(id, DocxTest.DOCX_XML_EXT, actual);
    }

}
