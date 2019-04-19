package com.example.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.example.domain.Pet;
import com.example.domain.*; // for static metamodels
import com.example.repository.PetRepository;
import com.example.repository.search.PetSearchRepository;
import com.example.service.dto.PetCriteria;

/**
 * Service for executing complex queries for Pet entities in the database.
 * The main input is a {@link PetCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Pet} or a {@link Page} of {@link Pet} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PetQueryService extends QueryService<Pet> {

    private final Logger log = LoggerFactory.getLogger(PetQueryService.class);

    private final PetRepository petRepository;

    private final PetSearchRepository petSearchRepository;

    public PetQueryService(PetRepository petRepository, PetSearchRepository petSearchRepository) {
        this.petRepository = petRepository;
        this.petSearchRepository = petSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Pet} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Pet> findByCriteria(PetCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Pet} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Pet> findByCriteria(PetCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PetCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Pet> specification = createSpecification(criteria);
        return petRepository.count(specification);
    }

    /**
     * Function to convert PetCriteria to a {@link Specification}
     */
    private Specification<Pet> createSpecification(PetCriteria criteria) {
        Specification<Pet> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Pet_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Pet_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Pet_.type));
            }
            if (criteria.getBreed() != null) {
                specification = specification.and(buildStringSpecification(criteria.getBreed(), Pet_.breed));
            }
            if (criteria.getOwnerId() != null) {
                specification = specification.and(buildSpecification(criteria.getOwnerId(),
                    root -> root.join(Pet_.owner, JoinType.LEFT).get(Owner_.id)));
            }
        }
        return specification;
    }
}
