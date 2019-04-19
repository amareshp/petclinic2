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

import com.example.domain.Slot;
import com.example.domain.*; // for static metamodels
import com.example.repository.SlotRepository;
import com.example.repository.search.SlotSearchRepository;
import com.example.service.dto.SlotCriteria;

/**
 * Service for executing complex queries for Slot entities in the database.
 * The main input is a {@link SlotCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Slot} or a {@link Page} of {@link Slot} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SlotQueryService extends QueryService<Slot> {

    private final Logger log = LoggerFactory.getLogger(SlotQueryService.class);

    private final SlotRepository slotRepository;

    private final SlotSearchRepository slotSearchRepository;

    public SlotQueryService(SlotRepository slotRepository, SlotSearchRepository slotSearchRepository) {
        this.slotRepository = slotRepository;
        this.slotSearchRepository = slotSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Slot} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Slot> findByCriteria(SlotCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Slot> specification = createSpecification(criteria);
        return slotRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Slot} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Slot> findByCriteria(SlotCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Slot> specification = createSpecification(criteria);
        return slotRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SlotCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Slot> specification = createSpecification(criteria);
        return slotRepository.count(specification);
    }

    /**
     * Function to convert SlotCriteria to a {@link Specification}
     */
    private Specification<Slot> createSpecification(SlotCriteria criteria) {
        Specification<Slot> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Slot_.id));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStartTime(), Slot_.startTime));
            }
        }
        return specification;
    }
}
