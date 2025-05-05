package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.legislation.util.Links;

class FragmentIdentifierExtractionTest {

    @Test
	void fragments() {

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
