package com.example.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Appointment.
 */
@Entity
@Table(name = "appointment")
@Document(indexName = "appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "appt_time", nullable = false)
    private LocalDate apptTime;

    @ManyToOne
    @JsonIgnoreProperties("appointments")
    private Slot slot;

    @ManyToOne
    @JsonIgnoreProperties("appointments")
    private Vet vet;

    @ManyToOne
    @JsonIgnoreProperties("appointments")
    private Pet pet;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getApptTime() {
        return apptTime;
    }

    public Appointment apptTime(LocalDate apptTime) {
        this.apptTime = apptTime;
        return this;
    }

    public void setApptTime(LocalDate apptTime) {
        this.apptTime = apptTime;
    }

    public Slot getSlot() {
        return slot;
    }

    public Appointment slot(Slot slot) {
        this.slot = slot;
        return this;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Vet getVet() {
        return vet;
    }

    public Appointment vet(Vet vet) {
        this.vet = vet;
        return this;
    }

    public void setVet(Vet vet) {
        this.vet = vet;
    }

    public Pet getPet() {
        return pet;
    }

    public Appointment pet(Pet pet) {
        this.pet = pet;
        return this;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Appointment appointment = (Appointment) o;
        if (appointment.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), appointment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", apptTime='" + getApptTime() + "'" +
            "}";
    }
}
