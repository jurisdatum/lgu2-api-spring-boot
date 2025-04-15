package uk.gov.legislation.transform;

import net.sf.saxon.s9api.SaxonApiException;
import org.springframework.stereotype.Service;
import uk.gov.legislation.transform.clml2docx.Clml2Docx;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class Transforms {

    private final Clml2Docx clml2docx;

    public Transforms(Clml2Docx clml2docx) {
        this.clml2docx = clml2docx;
    }

    public byte[] clml2docx(String clml) throws IOException, SaxonApiException {
        ByteArrayInputStream input = new ByteArrayInputStream(clml.getBytes());
        return clml2docx.transform(input);
    }

}
