package project.elasticsearch.infrastructure.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Elasticsearch document model.
 * This is the infrastructure layer representation of our domain Document model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "documents")
public class ElasticsearchDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text, name = "title")
    private String title;
    
    @Field(type = FieldType.Text, name = "content")
    private String content;
    
    @Field(type = FieldType.Keyword, name = "author")
    private String author;
    
    @Field(type = FieldType.Date, name = "created_at")
    private String createdAt;
    
    /**
     * Convert from domain model to Elasticsearch document
     * @param document Domain model
     * @return Elasticsearch document
     */
    public static ElasticsearchDocument fromDomain(project.elasticsearch.domain.model.Document document) {
        return ElasticsearchDocument.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .author(document.getAuthor())
                .createdAt(document.getCreatedAt())
                .build();
    }
    
    /**
     * Convert to domain model
     * @return Domain model
     */
    public project.elasticsearch.domain.model.Document toDomain() {
        return project.elasticsearch.domain.model.Document.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .author(this.author)
                .createdAt(this.createdAt)
                .build();
    }
}