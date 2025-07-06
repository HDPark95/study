package project.elasticsearch.infrastructure.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data Elasticsearch repository interface.
 * This extends the ElasticsearchRepository to provide basic CRUD operations.
 */
@Repository
public interface SpringDataElasticsearchRepository extends ElasticsearchRepository<ElasticsearchDocument, String> {
}