package project.elasticsearch.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.elasticsearch.domain.model.Document;
import project.elasticsearch.domain.repository.DocumentRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for document operations.
 * This is part of the application layer in Clean Architecture.
 */
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Create a new document
     * @param title Document title
     * @param content Document content
     * @param author Document author
     * @return The created document
     */
    public Document createDocument(String title, String content, String author) {
        Document document = Document.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .content(content)
                .author(author)
                .createdAt(LocalDateTime.now().format(DATE_FORMATTER))
                .build();
        
        return documentRepository.save(document);
    }

    /**
     * Get a document by ID
     * @param id Document ID
     * @return The document if found
     */
    public Optional<Document> getDocumentById(String id) {
        return documentRepository.findById(id);
    }

    /**
     * Get all documents
     * @return List of all documents
     */
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    /**
     * Delete a document by ID
     * @param id Document ID
     */
    public void deleteDocument(String id) {
        documentRepository.deleteById(id);
    }

    /**
     * Search for documents
     * @param query Search query
     * @return List of matching documents
     */
    public List<Document> searchDocuments(String query) {
        return documentRepository.search(query);
    }

    /**
     * Get Elasticsearch index information
     * @return Index information as a string
     */
    public String getIndexInfo() {
        return documentRepository.getIndexInfo();
    }
}