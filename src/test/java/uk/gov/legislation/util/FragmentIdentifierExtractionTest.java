package uk.gov.legislation.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

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

    @Test
    void crossheadingIdentifiersRemainIntact() {
        // crossheading fragments with multi-word titles must preserve dashes
        // (e.g., "final-provisions" not "final/provisions").
        String link = "http://www.legislation.gov.uk/ukpga/2023/29/part/1/chapter/2/crossheading/final-provisions";
        Assertions.assertEquals(
            "part/1/chapter/2/crossheading/final-provisions",
            Links.extractFragmentIdentifierFromLink(link)
        );

        URI uri = URI.create(link);
        Assertions.assertEquals(
            "part/1/chapter/2/crossheading/final-provisions",
            Links.extractFragmentIdentifierFromLink(uri)
        );
    }

}
