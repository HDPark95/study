package project.elasticsearch.interfaces.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.elasticsearch.application.service.DocumentService;
import project.elasticsearch.domain.model.Document;
import project.elasticsearch.interfaces.dto.DocumentRequest;
import project.elasticsearch.interfaces.dto.DocumentResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for document operations.
 * This is part of the interfaces layer in Clean Architecture.
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Create a new document
     * @param request Document creation request
     * @return The created document
     */
    @PostMapping
    public ResponseEntity<DocumentResponse> createDocument(@RequestBody DocumentRequest request) {
        Document document = documentService.createDocument(
                request.getTitle(),
                request.getContent(),
                request.getAuthor()
        );
        return new ResponseEntity<>(DocumentResponse.fromDomain(document), HttpStatus.CREATED);
    }

    /**
     * Get a document by ID
     * @param id Document ID
     * @return The document if found
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable String id) {
        return documentService.getDocumentById(id)
                .map(document -> new ResponseEntity<>(DocumentResponse.fromDomain(document), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Get all documents
     * @return List of all documents
     */
    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        List<DocumentResponse> documents = documentService.getAllDocuments().stream()
                .map(DocumentResponse::fromDomain)
                .collect(Collectors.toList());
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    /**
     * Delete a document by ID
     * @param id Document ID
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable String id) {
        documentService.deleteDocument(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Search for documents
     * @param query Search query
     * @return List of matching documents
     */
    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(@RequestParam String query) {
        List<DocumentResponse> documents = documentService.searchDocuments(query).stream()
                .map(DocumentResponse::fromDomain)
                .collect(Collectors.toList());
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    /**
     * Get Elasticsearch index information using the cat API
     * @return Index information as a string
     */
    @GetMapping("/index-info")
    public ResponseEntity<String> getIndexInfo() {
        String indexInfo = documentService.getIndexInfo();
        return new ResponseEntity<>(indexInfo, HttpStatus.OK);
    }
}