package com.example.repository.search;

import com.example.domain.Vet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Vet entity.
 */
public interface VetSearchRepository extends ElasticsearchRepository<Vet, Long> {
}
