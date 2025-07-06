package project.elasticsearch.domain.repository;

import project.elasticsearch.domain.model.Document;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Document operations.
 * This follows the Dependency Inversion Principle of Clean Architecture.
 */
public interface DocumentRepository {
    /**
     * Index a document in Elasticsearch
     * @param document The document to index
     * @return The indexed document with generated ID
     */
    Document save(Document document);
    
    /**
     * Find a document by its ID
     * @param id The document ID
     * @return The document if found
     */
    Optional<Document> findById(String id);
    
    /**
     * Find all documents
     * @return List of all documents
     */
    List<Document> findAll();
    
    /**
     * Delete a document by its ID
     * @param id The document ID
     */
    void deleteById(String id);
    
    /**
     * Search for documents by a query string
     * @param query The search query
     * @return List of matching documents
     */
    List<Document> search(String query);
    
    /**
     * Get Elasticsearch index information using the cat API
     * @return Index information as a string
     */
    String getIndexInfo();
}