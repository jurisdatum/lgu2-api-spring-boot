package uk.gov.legislation.endpoints.opensearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.legislation.data.virtuoso.model.OpenSearchRequest;


import java.io.IOException;
import java.util.UUID;

@Service
public class OpenSearchService {
    @Value("${opensearch.index.name}")
    private String indexName;
    private final RestHighLevelClient openSearchClient;
    private final ObjectMapper objectMapper;


    public OpenSearchService(RestHighLevelClient openSearchClient, ObjectMapper objectMapper) {
        this.openSearchClient = openSearchClient;
        this.objectMapper = objectMapper;
    }

    public String get(String title, String type, Integer year, Integer number, String language, int page, int size) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (title != null) {
            boolQuery.must(QueryBuilders.matchQuery("title", title));
        }
        if (type != null) {
            boolQuery.must(QueryBuilders.termQuery("type", type));
        }
        if (year != null) {
            boolQuery.must(QueryBuilders.termQuery("year", year));
        }
        if (number != null) {
            boolQuery.must(QueryBuilders.termQuery("number", number));
        }
        if (language != null) {
            boolQuery.must(QueryBuilders.termQuery("language", language));
        }

        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(boolQuery);
        searchSourceBuilder.from(page * size);
        searchSourceBuilder.size(size);
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

        /// Convert results to JSON array string
        ArrayNode resultArray = objectMapper.createArrayNode();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(hit.getSourceAsString());
            jsonNode.put("id", hit.getId());
            resultArray.add(jsonNode);
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultArray);
    }

    public String getAllDocuments() throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = openSearchClient.search(searchRequest, RequestOptions.DEFAULT);

        ArrayNode resultArray = objectMapper.createArrayNode();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            /// Convert each hit to JSON ObjectNode and add to array
            ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(hit.getSourceAsString());
            jsonNode.put("id", hit.getId());
            resultArray.add(jsonNode);
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultArray);
    }


    public String getDocumentById(String documentId) throws IOException {
        GetRequest getRequest = new GetRequest(indexName, documentId);
        GetResponse getResponse = openSearchClient.get(getRequest, RequestOptions.DEFAULT);

        if (getResponse.isExists()) {
            return getResponse.getSourceAsString();
        } else {
            return "Document not found with ID: " + documentId;
        }
    }

    public String saveDocument(OpenSearchRequest document) throws IOException {
        /// Generate ID if not provided
        if (document.getId() == null || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }

        /// Convert the Document object to JSON
        String jsonString = objectMapper.writeValueAsString(document);

        IndexRequest indexRequest = new IndexRequest(indexName)
                .id(document.getId())
                .source(jsonString, XContentType.JSON);

        IndexResponse indexResponse = openSearchClient.index(indexRequest, RequestOptions.DEFAULT);

        return indexResponse.getId();
    }

    public String deleteDocument(String documentId) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(indexName, documentId);
        DeleteResponse deleteResponse = openSearchClient.delete(deleteRequest, RequestOptions.DEFAULT);
        return deleteResponse.getResult().name();
    }
}