package com.example.web.rest;
import com.example.domain.Vet;
import com.example.service.VetService;
import com.example.web.rest.errors.BadRequestAlertException;
import com.example.web.rest.util.HeaderUtil;
import com.example.web.rest.util.PaginationUtil;
import com.example.service.dto.VetCriteria;
import com.example.service.VetQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Vet.
 */
@RestController
@RequestMapping("/api")
public class VetResource {

    private final Logger log = LoggerFactory.getLogger(VetResource.class);

    private static final String ENTITY_NAME = "vet";

    private final VetService vetService;

    private final VetQueryService vetQueryService;

    public VetResource(VetService vetService, VetQueryService vetQueryService) {
        this.vetService = vetService;
        this.vetQueryService = vetQueryService;
    }

    /**
     * POST  /vets : Create a new vet.
     *
     * @param vet the vet to create
     * @return the ResponseEntity with status 201 (Created) and with body the new vet, or with status 400 (Bad Request) if the vet has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/vets")
    public ResponseEntity<Vet> createVet(@RequestBody Vet vet) throws URISyntaxException {
        log.debug("REST request to save Vet : {}", vet);
        if (vet.getId() != null) {
            throw new BadRequestAlertException("A new vet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Vet result = vetService.save(vet);
        return ResponseEntity.created(new URI("/api/vets/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /vets : Updates an existing vet.
     *
     * @param vet the vet to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated vet,
     * or with status 400 (Bad Request) if the vet is not valid,
     * or with status 500 (Internal Server Error) if the vet couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/vets")
    public ResponseEntity<Vet> updateVet(@RequestBody Vet vet) throws URISyntaxException {
        log.debug("REST request to update Vet : {}", vet);
        if (vet.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Vet result = vetService.save(vet);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vet.getId().toString()))
            .body(result);
    }

    /**
     * GET  /vets : get all the vets.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of vets in body
     */
    @GetMapping("/vets")
    public ResponseEntity<List<Vet>> getAllVets(VetCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Vets by criteria: {}", criteria);
        Page<Vet> page = vetQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/vets");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * GET  /vets/count : count all the vets.
    *
    * @param criteria the criterias which the requested entities should match
    * @return the ResponseEntity with status 200 (OK) and the count in body
    */
    @GetMapping("/vets/count")
    public ResponseEntity<Long> countVets(VetCriteria criteria) {
        log.debug("REST request to count Vets by criteria: {}", criteria);
        return ResponseEntity.ok().body(vetQueryService.countByCriteria(criteria));
    }

    /**
     * GET  /vets/:id : get the "id" vet.
     *
     * @param id the id of the vet to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the vet, or with status 404 (Not Found)
     */
    @GetMapping("/vets/{id}")
    public ResponseEntity<Vet> getVet(@PathVariable Long id) {
        log.debug("REST request to get Vet : {}", id);
        Optional<Vet> vet = vetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(vet);
    }

    /**
     * DELETE  /vets/:id : delete the "id" vet.
     *
     * @param id the id of the vet to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/vets/{id}")
    public ResponseEntity<Void> deleteVet(@PathVariable Long id) {
        log.debug("REST request to delete Vet : {}", id);
        vetService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/vets?query=:query : search for the vet corresponding
     * to the query.
     *
     * @param query the query of the vet search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/vets")
    public ResponseEntity<List<Vet>> searchVets(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Vets for query {}", query);
        Page<Vet> page = vetService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/vets");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
