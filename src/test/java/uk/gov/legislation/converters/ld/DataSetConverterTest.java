package uk.gov.legislation.converters.ld;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.legislation.api.responses.ld.DataSet;
import uk.gov.legislation.data.virtuoso.jsonld.DatasetLD;
import uk.gov.legislation.data.virtuoso.jsonld.ValueAndType;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

 class DataSetConverterTest {


     @ParameterizedTest(name = "{index} => id={0}, types={1}, title={2}, expectedType={3}")
     @MethodSource("provideDatasetLDInputs")
     @DisplayName("Test DataSetConverter.convert with various DatasetLD inputs")
     void testConvert(DatasetLD input, URI expectedUri, String expectedType, String expectedTitle,
         ZonedDateTime expectedCreated, ZonedDateTime expectedModified) {

         DataSet result = DataSetConverter.convert(input);

         assertNotNull(result);
         assertEquals(expectedUri, result.uri);
         assertEquals(expectedType, result.type);
         assertEquals(expectedTitle, result.title);
         assertEquals(expectedCreated, result.created);
         assertEquals(expectedModified, result.modified);
     }

     static Stream <Arguments> provideDatasetLDInputs() {
         return Stream.of(
             // Case: Type matches known pattern, other fields null
             Arguments.of(
                 newDatasetLD(
                     URI.create("http://www.legislation.gov.uk/id/456"),
                     List.of(URI.create("http://www.legislation.gov.uk/def/legislation/Regulation")),
                     null, null, null
                 ),
                 URI.create("http://www.legislation.gov.uk/id/456"),
                 "Regulation",
                 null,
                 null,
                 null
             ),
             // Case: Type does not match pattern, title provided
             Arguments.of(
                 newDatasetLD(
                     URI.create("http://www.legislation.gov.uk/id/789"),
                     List.of(URI.create("http://example.com/otherType")),
                     "Another Title", null, null
                 ),
                 URI.create("http://www.legislation.gov.uk/id/789"),
                 null,
                 "Another Title",
                 null,
                 null
             )
         );
     }

     // Helper method to create DatasetLD
     private static DatasetLD newDatasetLD(URI id, List<URI> types, String title, ValueAndType created, ValueAndType modified) {
         DatasetLD datasetLD = new DatasetLD();
         datasetLD.id = id;
         datasetLD.types = types;
         datasetLD.title = title;
         datasetLD.created = created;
         datasetLD.modified = modified;
         return datasetLD;
     }
}