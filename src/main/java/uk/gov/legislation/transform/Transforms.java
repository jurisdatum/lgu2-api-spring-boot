package uk.gov.legislation.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.saxon.s9api.Destination;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.s9api.XsltTransformer;
import org.springframework.stereotype.Service;
import uk.gov.legislation.api.responses.*;
import uk.gov.legislation.converters.DocumentMetadataConverter;
import uk.gov.legislation.converters.FragmentMetadataConverter;
import uk.gov.legislation.converters.TableOfContentsConverter;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;
import uk.gov.legislation.transform.simple.Contents;
import uk.gov.legislation.transform.simple.Metadata;
import uk.gov.legislation.transform.simple.Simplify;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class Transforms {

    private final Clml2Akn clml2akn;

    private final Akn2Html akn2html;

    private final Simplify simplifier;

    private final Clml2Docx clml2docx;

    public Transforms(Clml2Akn clml2akn, Akn2Html akn2html, Simplify simplify, Clml2Docx clml2docx) {
        this.clml2akn = clml2akn;
        this.akn2html = akn2html;
        this.simplifier = simplify;
        this.clml2docx = clml2docx;
    }

    public String clml2akn(String clml) throws SaxonApiException {
        XdmNode aknNode = clml2akn.transform(clml);
        return Clml2Akn.serialize(aknNode);
    }

    public void clml2akn(InputStream clml, OutputStream akn) {
        try {
            clml2akn.transform(clml, akn);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error in AkN transform", e);
        }
    }

    public String clml2html(String clml, boolean standalone) throws SaxonApiException {
        XdmNode akn = clml2akn.transform(clml);
        return akn2html.transform(akn, standalone);
    }

    public void clml2html(InputStream clml, boolean standalone, OutputStream html) {
        Destination next = akn2html.asDestination(standalone, html);
        try {
            clml2akn.transform(clml, next);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error in HTML transform", e);
        }
    }
    public void clml2htmlStandalone(InputStream clml, OutputStream html) {
        clml2html(clml, true, html);
    }

    public Document clml2document(String clml) throws SaxonApiException, JsonProcessingException {
        XdmNode doc = Helper.parse(clml);
        XdmNode akn = clml2akn.transform(doc);
        String html = akn2html.transform(akn, false);
        Metadata simple = simplifier.extractDocumentMetadata(doc);
        DocumentMetadata converted = DocumentMetadataConverter.convert(simple);
        return new Document(converted, html);
    }

    public Document clml2document(InputStream clml) throws SaxonApiException, JsonProcessingException {
        XdmNode doc = Helper.parse(clml);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XsltTransformer next = akn2html.asDestination(false, baos);
        clml2akn.transform(doc, next);
        String html = baos.toString(StandardCharsets.UTF_8);
        Metadata simple = simplifier.extractDocumentMetadata(doc);
        DocumentMetadata converted = DocumentMetadataConverter.convert(simple);
        return new Document(converted, html);
    }

    public Fragment clml2fragment(String clml) throws SaxonApiException, JsonProcessingException {
        XdmNode doc = Helper.parse(clml);
        XdmNode akn = clml2akn.transform(doc);
        String html = akn2html.transform(akn, false);
        Metadata simple = simplifier.extractFragmentMetadata(doc);
        FragmentMetadata converted = FragmentMetadataConverter.convert(simple);
        return new Fragment(converted, html);
    }

    public TableOfContents clml2toc(String clml) throws SaxonApiException, JsonProcessingException {
        Contents simple = simplifier.contents(clml);
        return TableOfContentsConverter.convert(simple);
    }

    public byte[] clml2docx(String clml) throws IOException, SaxonApiException {
        ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
        return clml2docx.transform(input);
    }

    public void clml2docx(InputStream clml, OutputStream docx) {
        try {
            clml2docx.transform(clml, docx);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (SaxonApiException e) {
            throw new TransformationException("Error in Word transform", e);
        }
    }

}
