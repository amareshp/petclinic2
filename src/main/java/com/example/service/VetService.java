package com.example.service;

import com.example.domain.Vet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Vet.
 */
public interface VetService {

    /**
     * Save a vet.
     *
     * @param vet the entity to save
     * @return the persisted entity
     */
    Vet save(Vet vet);

    /**
     * Get all the vets.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Vet> findAll(Pageable pageable);


    /**
     * Get the "id" vet.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Vet> findOne(Long id);

    /**
     * Delete the "id" vet.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the vet corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Vet> search(String query, Pageable pageable);
}
