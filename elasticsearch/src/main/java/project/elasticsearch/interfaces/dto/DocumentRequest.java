package project.elasticsearch.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for document creation requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    private String title;
    private String content;
    private String author;
}