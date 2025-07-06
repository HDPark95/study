package project.elasticsearch.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Repository;
import project.elasticsearch.domain.model.Document;
import project.elasticsearch.domain.repository.DocumentRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Implementation of DocumentRepository using Spring Data Elasticsearch.
 */
@Repository
@RequiredArgsConstructor
public class ElasticsearchDocumentRepository implements DocumentRepository {

    private final SpringDataElasticsearchRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RestClient restClient;
    
    private static final String INDEX_NAME = "documents";

    @Override
    public Document save(Document document) {
        ElasticsearchDocument esDocument = ElasticsearchDocument.fromDomain(document);
        ElasticsearchDocument savedDocument = repository.save(esDocument);
        return savedDocument.toDomain();
    }

    @Override
    public Optional<Document> findById(String id) {
        return repository.findById(id).map(ElasticsearchDocument::toDomain);
    }

    @Override
    public List<Document> findAll() {
        Iterable<ElasticsearchDocument> documents = repository.findAll();
        return StreamSupport.stream(documents.spliterator(), false)
                .map(ElasticsearchDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Document> search(String query) {
        Query searchQuery = new StringQuery(
                "{\"multi_match\": {\"query\": \"" + query + "\", \"fields\": [\"title\", \"content\"]}}"
        );
        
        SearchHits<ElasticsearchDocument> searchHits = 
                elasticsearchOperations.search(searchQuery, ElasticsearchDocument.class, IndexCoordinates.of(INDEX_NAME));
        
        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(ElasticsearchDocument::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public String getIndexInfo() {
        try {
            Request request = new Request(
                    "GET",
                    "/_cat/indices/" + INDEX_NAME + "?v=true&format=json"
            );
            
            Response response = restClient.performRequest(request);

            java.io.InputStream inputStream = response.getEntity().getContent();
            return new String(inputStream.readAllBytes());
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to get index information", e);
        }
    }
}