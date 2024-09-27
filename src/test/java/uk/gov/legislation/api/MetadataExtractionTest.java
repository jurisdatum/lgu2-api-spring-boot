package uk.gov.legislation.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.sf.saxon.s9api.SaxonApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import uk.gov.legislation.api.document.TableOfContents;
import uk.gov.legislation.transform.simple.Simplify;

import java.util.List;

class MetadataExtractionTest {

    private static String CLML = """
			<Legislation xmlns="http://www.legislation.gov.uk/namespaces/legislation">
			    <Metadata xmlns="http://www.legislation.gov.uk/namespaces/metadata" xmlns:dct="http://purl.org/dc/terms/" xmlns:atom="http://www.w3.org/2005/Atom">
				    <dct:valid>2017-03-16</dct:valid>
			        <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukpga/2017/1/enacted/revision" title="enacted" />
			        <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukpga/2017/1/2017-01-16/revision" title="2017-01-16" />
			        <atom:link rel="http://purl.org/dc/terms/hasVersion" href="http://www.legislation.gov.uk/ukpga/2017/1/revision" title="current" />
			        <PrimaryMetadata>
						<DocumentClassification>
							<DocumentCategory Value="primary" />
							<DocumentMainType Value="UnitedKingdomPublicGeneralAct" />
							<DocumentStatus Value="revised" />
						</DocumentClassification>
						<Year Value="2017" />
						<Number Value="1" />
						<EnactmentDate Date="2017-01-16" />
						<ISBN Value="9780105400639" />
					</PrimaryMetadata>
				</Metadata>
			</Legislation>
			""";
    @Test
    public void versions() throws SaxonApiException, JsonProcessingException {
		TableOfContents simple = new Simplify().contents(CLML);
		List<String> versions = simple.meta().versions();
		Assertions.assertEquals(3, versions.size(), "There should be exactly three versions");
		Assertions.assertEquals("enacted", versions.get(0), "First version should be 'enacted'");
		Assertions.assertEquals("2017-01-16", versions.get(1), "Second version should be '2017-01-16'");
		Assertions.assertEquals("2017-03-16", versions.get(2), "Third version should be '2017-03-16'");
		ObjectMapper mapper = new ObjectMapper().registerModules(new JavaTimeModule());
		String json = mapper.writeValueAsString(simple);
		Assertions.assertTrue(json.contains("\"versions\":[\"enacted\",\"2017-01-16\",\"2017-03-16\""));
    }

}
