package project.elasticsearch.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a document to be indexed in Elasticsearch.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    private String id;
    private String title;
    private String content;
    private String author;
    private String createdAt;
}