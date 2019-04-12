package com.example.repository.search;

import com.example.domain.Owner;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Owner entity.
 */
public interface OwnerSearchRepository extends ElasticsearchRepository<Owner, Long> {
}
