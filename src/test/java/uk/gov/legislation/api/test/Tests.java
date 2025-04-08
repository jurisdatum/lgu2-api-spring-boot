package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.opensearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.Application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = Application.class)
class Tests {

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	void contextLoads()  {
		assertNotNull(restHighLevelClient, "RestHighLevelClient should be autowired");


	}
}
