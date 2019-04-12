package com.example.service.impl;

import com.example.service.VetService;
import com.example.domain.Vet;
import com.example.repository.VetRepository;
import com.example.repository.search.VetSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Vet.
 */
@Service
@Transactional
public class VetServiceImpl implements VetService {

    private final Logger log = LoggerFactory.getLogger(VetServiceImpl.class);

    private final VetRepository vetRepository;

    private final VetSearchRepository vetSearchRepository;

    public VetServiceImpl(VetRepository vetRepository, VetSearchRepository vetSearchRepository) {
        this.vetRepository = vetRepository;
        this.vetSearchRepository = vetSearchRepository;
    }

    /**
     * Save a vet.
     *
     * @param vet the entity to save
     * @return the persisted entity
     */
    @Override
    public Vet save(Vet vet) {
        log.debug("Request to save Vet : {}", vet);
        Vet result = vetRepository.save(vet);
        vetSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the vets.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Vet> findAll(Pageable pageable) {
        log.debug("Request to get all Vets");
        return vetRepository.findAll(pageable);
    }


    /**
     * Get one vet by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Vet> findOne(Long id) {
        log.debug("Request to get Vet : {}", id);
        return vetRepository.findById(id);
    }

    /**
     * Delete the vet by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Vet : {}", id);
        vetRepository.deleteById(id);
        vetSearchRepository.deleteById(id);
    }

    /**
     * Search for the vet corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Vet> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Vets for query {}", query);
        return vetSearchRepository.search(queryStringQuery(query), pageable);    }
}
