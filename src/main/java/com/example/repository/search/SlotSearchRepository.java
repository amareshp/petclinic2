package com.example.repository.search;

import com.example.domain.Slot;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Slot entity.
 */
public interface SlotSearchRepository extends ElasticsearchRepository<Slot, Long> {
}
