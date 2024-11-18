package uk.gov.legislation.endpoints.document.service;

import net.sf.saxon.s9api.XdmNode;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.marklogic.Legislation;
import uk.gov.legislation.data.marklogic.NoDocumentException;
import uk.gov.legislation.endpoints.document.TableOfContents;
import uk.gov.legislation.exceptions.TransformationException;
import uk.gov.legislation.transform.Clml2Akn;
import uk.gov.legislation.transform.simple.Simplify;
import uk.gov.legislation.util.Constants;
import java.util.Optional;

@Service
public class ContentsService {

    private final Legislation legislationRepository;
    private final Clml2Akn clmlToAknTransformer;
    private final Simplify simplifier;

    public ContentsService(Legislation legislationRepository, Clml2Akn clmlToAknTransformer, Simplify simplifier) {
        this.legislationRepository = legislationRepository;
        this.clmlToAknTransformer = clmlToAknTransformer;
        this.simplifier = simplifier;
    }


    public String fetchContentsXml(String type, int year, int number, Optional <String> version) throws NoDocumentException {
        return Optional.ofNullable(legislationRepository.getTableOfContents(type, year, number, version))
                .orElseThrow(() -> new NoDocumentException(String.format(Constants.DOCUMENT_NOT_FOUND.getError(), type, year, number)));
    }

    public String transformToAkn(String clmlContent) {
        try {
            XdmNode transformedNode = clmlToAknTransformer.transform(clmlContent);
            return Clml2Akn.serialize(transformedNode);
        } catch (Exception e) {
            throw new TransformationException("Transformation to AKN format failed",e);
        }
    }

    public TableOfContents simplifyToTableOfContents(String clmlContent) {
        try {
            return simplifier.contents(clmlContent);
        } catch (Exception e) {
            throw new TransformationException("Simplification to JSON format failed",e);
        }
    }
}
