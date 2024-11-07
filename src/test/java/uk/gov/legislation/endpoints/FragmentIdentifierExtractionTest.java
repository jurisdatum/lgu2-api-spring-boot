package uk.gov.legislation.endpoints;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import uk.gov.legislation.util.Links;

public class FragmentIdentifierExtractionTest {

    @Test
	public void fragments() {

		String link = "http://www.legislation.gov.uk/ukpga/2017/1/2017-03-16/revision";
		String fragment = Links.extractFragmentIdentifierFromLink(link);
		Assertions.assertNull(fragment);

        link = "http://www.legislation.gov.uk/ukpga/2017/1/contents/enacted/revision";
        fragment = Links.extractFragmentIdentifierFromLink(link);
        Assertions.assertNull(fragment);

		link = "http://www.legislation.gov.uk/ukpga/2017/1/section/1/2017-03-16";
		fragment = Links.extractFragmentIdentifierFromLink(link);
		Assertions.assertEquals("section/1", fragment);

	}

}
