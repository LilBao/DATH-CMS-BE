package com.cms.repository.search;

import com.cms.entity.search.GenreDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GenreSearchRepository extends ElasticsearchRepository<GenreDocument, String> {
    List<GenreDocument> findByGenreNameContainingIgnoreCase(String genreName);
}
