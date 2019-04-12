package com.example.service.impl;

import com.example.service.AppointmentService;
import com.example.domain.Appointment;
import com.example.repository.AppointmentRepository;
import com.example.repository.search.AppointmentSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Appointment.
 */
@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;

    private final AppointmentSearchRepository appointmentSearchRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, AppointmentSearchRepository appointmentSearchRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentSearchRepository = appointmentSearchRepository;
    }

    /**
     * Save a appointment.
     *
     * @param appointment the entity to save
     * @return the persisted entity
     */
    @Override
    public Appointment save(Appointment appointment) {
        log.debug("Request to save Appointment : {}", appointment);
        Appointment result = appointmentRepository.save(appointment);
        appointmentSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the appointments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> findAll(Pageable pageable) {
        log.debug("Request to get all Appointments");
        return appointmentRepository.findAll(pageable);
    }


    /**
     * Get one appointment by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Appointment> findOne(Long id) {
        log.debug("Request to get Appointment : {}", id);
        return appointmentRepository.findById(id);
    }

    /**
     * Delete the appointment by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Appointment : {}", id);
        appointmentRepository.deleteById(id);
        appointmentSearchRepository.deleteById(id);
    }

    /**
     * Search for the appointment corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Appointments for query {}", query);
        return appointmentSearchRepository.search(queryStringQuery(query), pageable);    }
}
