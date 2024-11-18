package uk.gov.legislation.exceptions;

import net.sf.saxon.s9api.SaxonApiException;
import uk.gov.legislation.data.marklogic.NoDocumentException;

import java.io.IOException;
import java.net.URISyntaxException;

public class Exceptions {
    public static <T> T handleException(SupplierWithException<T> supplier) {
        try {
            return supplier.get();
        } catch (IOException e) {
            throw new RuntimeException("I/O error occurred during processing: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            throw new RuntimeException("Processing was interrupted: " + e.getMessage(), e);
        } catch (NoDocumentException e) {
            throw new RuntimeException("Document not found: " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException("URI syntax error occurred: " + e.getMessage(), e);
        } catch (SaxonApiException e) {
            throw new RuntimeException("Error during XSLT processing: " + e.getMessage(), e);
        }
    }


    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws IOException, InterruptedException, NoDocumentException,TransformationException, URISyntaxException, SaxonApiException;
    }
}
