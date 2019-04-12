package com.example.web.rest;
import com.example.domain.Slot;
import com.example.service.SlotService;
import com.example.web.rest.errors.BadRequestAlertException;
import com.example.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Slot.
 */
@RestController
@RequestMapping("/api")
public class SlotResource {

    private final Logger log = LoggerFactory.getLogger(SlotResource.class);

    private static final String ENTITY_NAME = "slot";

    private final SlotService slotService;

    public SlotResource(SlotService slotService) {
        this.slotService = slotService;
    }

    /**
     * POST  /slots : Create a new slot.
     *
     * @param slot the slot to create
     * @return the ResponseEntity with status 201 (Created) and with body the new slot, or with status 400 (Bad Request) if the slot has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/slots")
    public ResponseEntity<Slot> createSlot(@Valid @RequestBody Slot slot) throws URISyntaxException {
        log.debug("REST request to save Slot : {}", slot);
        if (slot.getId() != null) {
            throw new BadRequestAlertException("A new slot cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Slot result = slotService.save(slot);
        return ResponseEntity.created(new URI("/api/slots/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /slots : Updates an existing slot.
     *
     * @param slot the slot to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated slot,
     * or with status 400 (Bad Request) if the slot is not valid,
     * or with status 500 (Internal Server Error) if the slot couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/slots")
    public ResponseEntity<Slot> updateSlot(@Valid @RequestBody Slot slot) throws URISyntaxException {
        log.debug("REST request to update Slot : {}", slot);
        if (slot.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Slot result = slotService.save(slot);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, slot.getId().toString()))
            .body(result);
    }

    /**
     * GET  /slots : get all the slots.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of slots in body
     */
    @GetMapping("/slots")
    public List<Slot> getAllSlots() {
        log.debug("REST request to get all Slots");
        return slotService.findAll();
    }

    /**
     * GET  /slots/:id : get the "id" slot.
     *
     * @param id the id of the slot to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the slot, or with status 404 (Not Found)
     */
    @GetMapping("/slots/{id}")
    public ResponseEntity<Slot> getSlot(@PathVariable Long id) {
        log.debug("REST request to get Slot : {}", id);
        Optional<Slot> slot = slotService.findOne(id);
        return ResponseUtil.wrapOrNotFound(slot);
    }

    /**
     * DELETE  /slots/:id : delete the "id" slot.
     *
     * @param id the id of the slot to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/slots/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable Long id) {
        log.debug("REST request to delete Slot : {}", id);
        slotService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/slots?query=:query : search for the slot corresponding
     * to the query.
     *
     * @param query the query of the slot search
     * @return the result of the search
     */
    @GetMapping("/_search/slots")
    public List<Slot> searchSlots(@RequestParam String query) {
        log.debug("REST request to search Slots for query {}", query);
        return slotService.search(query);
    }

}
