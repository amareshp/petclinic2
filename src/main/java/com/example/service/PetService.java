package com.example.service;

import com.example.domain.Pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Pet.
 */
public interface PetService {

    /**
     * Save a pet.
     *
     * @param pet the entity to save
     * @return the persisted entity
     */
    Pet save(Pet pet);

    /**
     * Get all the pets.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Pet> findAll(Pageable pageable);


    /**
     * Get the "id" pet.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Pet> findOne(Long id);

    /**
     * Delete the "id" pet.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the pet corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Pet> search(String query, Pageable pageable);
}
