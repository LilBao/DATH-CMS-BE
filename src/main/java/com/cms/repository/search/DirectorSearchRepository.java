package com.cms.repository.search;

import com.cms.entity.search.DirectorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectorSearchRepository extends ElasticsearchRepository<DirectorDocument, String> {
    List<DirectorDocument> findByFullNameContainingIgnoreCase(String fullName);
}
