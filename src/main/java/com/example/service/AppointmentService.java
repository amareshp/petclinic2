package com.example.service;

import com.example.domain.Appointment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Appointment.
 */
public interface AppointmentService {

    /**
     * Save a appointment.
     *
     * @param appointment the entity to save
     * @return the persisted entity
     */
    Appointment save(Appointment appointment);

    /**
     * Get all the appointments.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Appointment> findAll(Pageable pageable);


    /**
     * Get the "id" appointment.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<Appointment> findOne(Long id);

    /**
     * Delete the "id" appointment.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the appointment corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<Appointment> search(String query, Pageable pageable);
}
