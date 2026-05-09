package com.cms.repository.search;

import com.cms.entity.search.ActorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorSearchRepository extends ElasticsearchRepository<ActorDocument, String> {
    List<ActorDocument> findByFullNameContainingIgnoreCase(String fullName);
}
