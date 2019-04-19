package com.example.repository.search;

import com.example.domain.Appointment;
import com.example.domain.Pet;
import com.example.domain.Slot;
import com.example.domain.Vet;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Spring Data Elasticsearch repository for the Appointment entity.
 */
public interface AppointmentSearchRepository extends ElasticsearchRepository<Appointment, Long> {
    List<Appointment> findBySlot(Slot slot);

    List<Appointment> findByVet(Vet vet);

    List<Appointment> findByPet(Pet pet);

}
