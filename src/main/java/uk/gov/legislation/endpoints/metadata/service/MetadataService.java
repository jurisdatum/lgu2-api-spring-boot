package uk.gov.legislation.endpoints.metadata.service;

import org.springframework.stereotype.Service;
import uk.gov.legislation.data.virtuoso.model.Item;

import java.io.IOException;

@Service
public class MetadataService {

    public Item fetchMetadata(String type, int year, int number) throws IOException, InterruptedException {
        return uk.gov.legislation.data.virtuoso.Metadata.get(type, year, number);
    }
}
