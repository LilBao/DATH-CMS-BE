package com.cms.entity.search;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Document(indexName = "movies", createIndex = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Integer)
    private Integer runTime;
    
    @Field(type = FieldType.Date)
    private LocalDate releaseDate;
    
    @Field(type = FieldType.Keyword)
    private List<String> genres;
    
    @Field(type = FieldType.Keyword)
    private List<String> actors;
    
    @Field(type = FieldType.Keyword)
    private List<String> formats;
    
    @Field(type = FieldType.Keyword)
    private String ageRating;

    @Field(type = FieldType.Text, index = false)
    private String posterUrl;
}
