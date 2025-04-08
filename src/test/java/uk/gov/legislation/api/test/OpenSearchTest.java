package uk.gov.legislation.api.test;

import org.junit.jupiter.api.Test;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.core.MainResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.legislation.endpoints.Application;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
class OpenSearchTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
     void testOpenSearchConnection() throws Exception {
        assertNotNull(restHighLevelClient, "RestHighLevelClient should be autowired");

        MainResponse response = restHighLevelClient.info(RequestOptions.DEFAULT);

        assertNotNull(response);
        System.out.println("Connected to OpenSearch version: " + response.getVersion().getNumber());
        assertTrue(response.getVersion().getNumber().startsWith("2"));
    }

    @Test
     void testPing() throws Exception {
        boolean isAvailable = restHighLevelClient.ping(RequestOptions.DEFAULT);
        assertTrue(isAvailable, "OpenSearch cluster should be reachable");
    }
}
