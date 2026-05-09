package com.cms.entity.search;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "genres", createIndex = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String genreName;
}
