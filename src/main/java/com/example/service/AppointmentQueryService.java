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

import com.example.domain.Appointment;
import com.example.domain.*; // for static metamodels
import com.example.repository.AppointmentRepository;
import com.example.repository.search.AppointmentSearchRepository;
import com.example.service.dto.AppointmentCriteria;

/**
 * Service for executing complex queries for Appointment entities in the database.
 * The main input is a {@link AppointmentCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Appointment} or a {@link Page} of {@link Appointment} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AppointmentQueryService extends QueryService<Appointment> {

    private final Logger log = LoggerFactory.getLogger(AppointmentQueryService.class);

    private final AppointmentRepository appointmentRepository;

    private final AppointmentSearchRepository appointmentSearchRepository;

    public AppointmentQueryService(AppointmentRepository appointmentRepository, AppointmentSearchRepository appointmentSearchRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentSearchRepository = appointmentSearchRepository;
    }

    /**
     * Return a {@link List} of {@link Appointment} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Appointment> findByCriteria(AppointmentCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Appointment> specification = createSpecification(criteria);
        return appointmentRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Appointment} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Appointment> findByCriteria(AppointmentCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Appointment> specification = createSpecification(criteria);
        return appointmentRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AppointmentCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Appointment> specification = createSpecification(criteria);
        return appointmentRepository.count(specification);
    }

    /**
     * Function to convert AppointmentCriteria to a {@link Specification}
     */
    private Specification<Appointment> createSpecification(AppointmentCriteria criteria) {
        Specification<Appointment> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Appointment_.id));
            }
            if (criteria.getApptTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getApptTime(), Appointment_.apptTime));
            }
            if (criteria.getSlotId() != null) {
                specification = specification.and(buildSpecification(criteria.getSlotId(),
                    root -> root.join(Appointment_.slot, JoinType.LEFT).get(Slot_.id)));
            }
            if (criteria.getVetId() != null) {
                specification = specification.and(buildSpecification(criteria.getVetId(),
                    root -> root.join(Appointment_.vet, JoinType.LEFT).get(Vet_.id)));
            }
            if (criteria.getPetId() != null) {
                specification = specification.and(buildSpecification(criteria.getPetId(),
                    root -> root.join(Appointment_.pet, JoinType.LEFT).get(Pet_.id)));
            }
        }
        return specification;
    }
}
