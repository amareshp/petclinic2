package com.example.repository.search;

import com.example.domain.Pet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Pet entity.
 */
public interface PetSearchRepository extends ElasticsearchRepository<Pet, Long> {
}
