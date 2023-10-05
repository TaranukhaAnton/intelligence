package ua.gov.intelligence.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import ua.gov.intelligence.domain.Frequency;
import ua.gov.intelligence.repository.FrequencyRepository;
import ua.gov.intelligence.service.FrequencyQueryService;
import ua.gov.intelligence.service.FrequencyService;
import ua.gov.intelligence.service.criteria.FrequencyCriteria;
import ua.gov.intelligence.web.rest.errors.BadRequestAlertException;

/**
 * REST controller for managing {@link ua.gov.intelligence.domain.Frequency}.
 */
@RestController
@RequestMapping("/api")
public class FrequencyResource {

    private final Logger log = LoggerFactory.getLogger(FrequencyResource.class);

    private static final String ENTITY_NAME = "frequency";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FrequencyService frequencyService;

    private final FrequencyRepository frequencyRepository;

    private final FrequencyQueryService frequencyQueryService;

    public FrequencyResource(
        FrequencyService frequencyService,
        FrequencyRepository frequencyRepository,
        FrequencyQueryService frequencyQueryService
    ) {
        this.frequencyService = frequencyService;
        this.frequencyRepository = frequencyRepository;
        this.frequencyQueryService = frequencyQueryService;
    }

    /**
     * {@code POST  /frequencies} : Create a new frequency.
     *
     * @param frequency the frequency to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new frequency, or with status {@code 400 (Bad Request)} if the frequency has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/frequencies")
    public ResponseEntity<Frequency> createFrequency(@RequestBody Frequency frequency) throws URISyntaxException {
        log.debug("REST request to save Frequency : {}", frequency);
        if (frequency.getId() != null) {
            throw new BadRequestAlertException("A new frequency cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Frequency result = frequencyService.save(frequency);
        return ResponseEntity
            .created(new URI("/api/frequencies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /frequencies/:id} : Updates an existing frequency.
     *
     * @param id the id of the frequency to save.
     * @param frequency the frequency to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated frequency,
     * or with status {@code 400 (Bad Request)} if the frequency is not valid,
     * or with status {@code 500 (Internal Server Error)} if the frequency couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/frequencies/{id}")
    public ResponseEntity<Frequency> updateFrequency(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Frequency frequency
    ) throws URISyntaxException {
        log.debug("REST request to update Frequency : {}, {}", id, frequency);
        if (frequency.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, frequency.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!frequencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Frequency result = frequencyService.update(frequency);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, frequency.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /frequencies/:id} : Partial updates given fields of an existing frequency, field will ignore if it is null
     *
     * @param id the id of the frequency to save.
     * @param frequency the frequency to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated frequency,
     * or with status {@code 400 (Bad Request)} if the frequency is not valid,
     * or with status {@code 404 (Not Found)} if the frequency is not found,
     * or with status {@code 500 (Internal Server Error)} if the frequency couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/frequencies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Frequency> partialUpdateFrequency(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Frequency frequency
    ) throws URISyntaxException {
        log.debug("REST request to partial update Frequency partially : {}, {}", id, frequency);
        if (frequency.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, frequency.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!frequencyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Frequency> result = frequencyService.partialUpdate(frequency);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, frequency.getId().toString())
        );
    }

    /**
     * {@code GET  /frequencies} : get all the frequencies.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of frequencies in body.
     */
    @GetMapping("/frequencies")
    public ResponseEntity<List<Frequency>> getAllFrequencies(
        FrequencyCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Frequencies by criteria: {}", criteria);
        Page<Frequency> page = frequencyQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/frequencies-all")
    public ResponseEntity<List<Frequency>> getAllFrequenciesNP(
        FrequencyCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Frequencies by criteria: {}", criteria);
        final List<Frequency> byCriteria = frequencyQueryService.findByCriteria(criteria);

        return ResponseEntity.ok().body(byCriteria);
    }

    /**
     * {@code GET  /frequencies/count} : count all the frequencies.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/frequencies/count")
    public ResponseEntity<Long> countFrequencies(FrequencyCriteria criteria) {
        log.debug("REST request to count Frequencies by criteria: {}", criteria);
        return ResponseEntity.ok().body(frequencyQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /frequencies/:id} : get the "id" frequency.
     *
     * @param id the id of the frequency to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the frequency, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/frequencies/{id}")
    public ResponseEntity<Frequency> getFrequency(@PathVariable Long id) {
        log.debug("REST request to get Frequency : {}", id);
        Optional<Frequency> frequency = frequencyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(frequency);
    }

    /**
     * {@code DELETE  /frequencies/:id} : delete the "id" frequency.
     *
     * @param id the id of the frequency to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/frequencies/{id}")
    public ResponseEntity<Void> deleteFrequency(@PathVariable Long id) {
        log.debug("REST request to delete Frequency : {}", id);
        frequencyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
