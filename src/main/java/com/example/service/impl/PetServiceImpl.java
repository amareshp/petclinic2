package com.example.service.impl;

import com.example.service.PetService;
import com.example.domain.Pet;
import com.example.repository.PetRepository;
import com.example.repository.search.PetSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Pet.
 */
@Service
@Transactional
public class PetServiceImpl implements PetService {

    private final Logger log = LoggerFactory.getLogger(PetServiceImpl.class);

    private final PetRepository petRepository;

    private final PetSearchRepository petSearchRepository;

    public PetServiceImpl(PetRepository petRepository, PetSearchRepository petSearchRepository) {
        this.petRepository = petRepository;
        this.petSearchRepository = petSearchRepository;
    }

    /**
     * Save a pet.
     *
     * @param pet the entity to save
     * @return the persisted entity
     */
    @Override
    public Pet save(Pet pet) {
        log.debug("Request to save Pet : {}", pet);
        Pet result = petRepository.save(pet);
        petSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the pets.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Pet> findAll(Pageable pageable) {
        log.debug("Request to get all Pets");
        return petRepository.findAll(pageable);
    }


    /**
     * Get one pet by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> findOne(Long id) {
        log.debug("Request to get Pet : {}", id);
        return petRepository.findById(id);
    }

    /**
     * Delete the pet by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Pet : {}", id);
        petRepository.deleteById(id);
        petSearchRepository.deleteById(id);
    }

    /**
     * Search for the pet corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Pet> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Pets for query {}", query);
        return petSearchRepository.search(queryStringQuery(query), pageable);    }
}
