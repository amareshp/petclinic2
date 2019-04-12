package com.example.service;

import com.example.domain.Slot;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing Slot.
 */
public interface SlotService {

    /**
     * Save a slot.
     *
     * @param slot the entity to save
     * @return the persisted entity
     */
    Slot save(Slot slot);

    /**
     * Get all the slots.
     *
     * @return the list of entities
     */
    List<Slot> findAll();


    /**
     * Get the "id" slot.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Slot> findOne(Long id);

    /**
     * Delete the "id" slot.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the slot corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<Slot> search(String query);
}
