package com.example.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the Appointment entity. This class is used in AppointmentResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /appointments?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AppointmentCriteria implements Serializable {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LocalDateFilter apptTime;

    private LongFilter slotId;

    private LongFilter vetId;

    private LongFilter petId;

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LocalDateFilter getApptTime() {
        return apptTime;
    }

    public void setApptTime(LocalDateFilter apptTime) {
        this.apptTime = apptTime;
    }

    public LongFilter getSlotId() {
        return slotId;
    }

    public void setSlotId(LongFilter slotId) {
        this.slotId = slotId;
    }

    public LongFilter getVetId() {
        return vetId;
    }

    public void setVetId(LongFilter vetId) {
        this.vetId = vetId;
    }

    public LongFilter getPetId() {
        return petId;
    }

    public void setPetId(LongFilter petId) {
        this.petId = petId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AppointmentCriteria that = (AppointmentCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(apptTime, that.apptTime) &&
            Objects.equals(slotId, that.slotId) &&
            Objects.equals(vetId, that.vetId) &&
            Objects.equals(petId, that.petId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        apptTime,
        slotId,
        vetId,
        petId
        );
    }

    @Override
    public String toString() {
        return "AppointmentCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (apptTime != null ? "apptTime=" + apptTime + ", " : "") +
                (slotId != null ? "slotId=" + slotId + ", " : "") +
                (vetId != null ? "vetId=" + vetId + ", " : "") +
                (petId != null ? "petId=" + petId + ", " : "") +
            "}";
    }

}
