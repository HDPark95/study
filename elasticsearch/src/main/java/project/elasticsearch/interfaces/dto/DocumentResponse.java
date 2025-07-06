package project.elasticsearch.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import project.elasticsearch.domain.model.Document;

/**
 * DTO for document responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private String id;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    
    /**
     * Convert from domain model to response DTO
     * @param document Domain model
     * @return Response DTO
     */
    public static DocumentResponse fromDomain(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .author(document.getAuthor())
                .createdAt(document.getCreatedAt())
                .build();
    }
}