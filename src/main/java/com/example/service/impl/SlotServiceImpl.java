package com.example.service.impl;

import com.example.service.SlotService;
import com.example.domain.Slot;
import com.example.repository.SlotRepository;
import com.example.repository.search.SlotSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Slot.
 */
@Service
@Transactional
public class SlotServiceImpl implements SlotService {

    private final Logger log = LoggerFactory.getLogger(SlotServiceImpl.class);

    private final SlotRepository slotRepository;

    private final SlotSearchRepository slotSearchRepository;

    public SlotServiceImpl(SlotRepository slotRepository, SlotSearchRepository slotSearchRepository) {
        this.slotRepository = slotRepository;
        this.slotSearchRepository = slotSearchRepository;
    }

    /**
     * Save a slot.
     *
     * @param slot the entity to save
     * @return the persisted entity
     */
    @Override
    public Slot save(Slot slot) {
        log.debug("Request to save Slot : {}", slot);
        Slot result = slotRepository.save(slot);
        slotSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the slots.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Slot> findAll() {
        log.debug("Request to get all Slots");
        return slotRepository.findAll();
    }


    /**
     * Get one slot by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Slot> findOne(Long id) {
        log.debug("Request to get Slot : {}", id);
        return slotRepository.findById(id);
    }

    /**
     * Delete the slot by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Slot : {}", id);
        slotRepository.deleteById(id);
        slotSearchRepository.deleteById(id);
    }

    /**
     * Search for the slot corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<Slot> search(String query) {
        log.debug("Request to search Slots for query {}", query);
        return StreamSupport
            .stream(slotSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
