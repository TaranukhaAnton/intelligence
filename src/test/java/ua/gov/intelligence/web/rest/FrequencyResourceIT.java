package ua.gov.intelligence.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.gov.intelligence.IntegrationTest;
import ua.gov.intelligence.domain.Frequency;
import ua.gov.intelligence.domain.TriangulationPoint;
import ua.gov.intelligence.repository.FrequencyRepository;
import ua.gov.intelligence.service.criteria.FrequencyCriteria;

/**
 * Integration tests for the {@link FrequencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FrequencyResourceIT {

    private static final Double DEFAULT_NAME = 1D;
    private static final Double UPDATED_NAME = 2D;
    private static final Double SMALLER_NAME = 1D - 1D;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/frequencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private FrequencyRepository frequencyRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFrequencyMockMvc;

    private Frequency frequency;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Frequency createEntity(EntityManager em) {
        Frequency frequency = new Frequency().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION);
        return frequency;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Frequency createUpdatedEntity(EntityManager em) {
        Frequency frequency = new Frequency().name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        return frequency;
    }

    @BeforeEach
    public void initTest() {
        frequency = createEntity(em);
    }

    @Test
    @Transactional
    void createFrequency() throws Exception {
        int databaseSizeBeforeCreate = frequencyRepository.findAll().size();
        // Create the Frequency
        restFrequencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(frequency)))
            .andExpect(status().isCreated());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeCreate + 1);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testFrequency.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createFrequencyWithExistingId() throws Exception {
        // Create the Frequency with an existing ID
        frequency.setId(1L);

        int databaseSizeBeforeCreate = frequencyRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFrequencyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(frequency)))
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllFrequencies() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(frequency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getFrequency() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get the frequency
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL_ID, frequency.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(frequency.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.doubleValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getFrequenciesByIdFiltering() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        Long id = frequency.getId();

        defaultFrequencyShouldBeFound("id.equals=" + id);
        defaultFrequencyShouldNotBeFound("id.notEquals=" + id);

        defaultFrequencyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultFrequencyShouldNotBeFound("id.greaterThan=" + id);

        defaultFrequencyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultFrequencyShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name equals to DEFAULT_NAME
        defaultFrequencyShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the frequencyList where name equals to UPDATED_NAME
        defaultFrequencyShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultFrequencyShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the frequencyList where name equals to UPDATED_NAME
        defaultFrequencyShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name is not null
        defaultFrequencyShouldBeFound("name.specified=true");

        // Get all the frequencyList where name is null
        defaultFrequencyShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name is greater than or equal to DEFAULT_NAME
        defaultFrequencyShouldBeFound("name.greaterThanOrEqual=" + DEFAULT_NAME);

        // Get all the frequencyList where name is greater than or equal to UPDATED_NAME
        defaultFrequencyShouldNotBeFound("name.greaterThanOrEqual=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name is less than or equal to DEFAULT_NAME
        defaultFrequencyShouldBeFound("name.lessThanOrEqual=" + DEFAULT_NAME);

        // Get all the frequencyList where name is less than or equal to SMALLER_NAME
        defaultFrequencyShouldNotBeFound("name.lessThanOrEqual=" + SMALLER_NAME);
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsLessThanSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name is less than DEFAULT_NAME
        defaultFrequencyShouldNotBeFound("name.lessThan=" + DEFAULT_NAME);

        // Get all the frequencyList where name is less than UPDATED_NAME
        defaultFrequencyShouldBeFound("name.lessThan=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllFrequenciesByNameIsGreaterThanSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where name is greater than DEFAULT_NAME
        defaultFrequencyShouldNotBeFound("name.greaterThan=" + DEFAULT_NAME);

        // Get all the frequencyList where name is greater than SMALLER_NAME
        defaultFrequencyShouldBeFound("name.greaterThan=" + SMALLER_NAME);
    }

    @Test
    @Transactional
    void getAllFrequenciesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where description equals to DEFAULT_DESCRIPTION
        defaultFrequencyShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the frequencyList where description equals to UPDATED_DESCRIPTION
        defaultFrequencyShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFrequenciesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultFrequencyShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the frequencyList where description equals to UPDATED_DESCRIPTION
        defaultFrequencyShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFrequenciesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where description is not null
        defaultFrequencyShouldBeFound("description.specified=true");

        // Get all the frequencyList where description is null
        defaultFrequencyShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllFrequenciesByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where description contains DEFAULT_DESCRIPTION
        defaultFrequencyShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the frequencyList where description contains UPDATED_DESCRIPTION
        defaultFrequencyShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFrequenciesByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        // Get all the frequencyList where description does not contain DEFAULT_DESCRIPTION
        defaultFrequencyShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the frequencyList where description does not contain UPDATED_DESCRIPTION
        defaultFrequencyShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllFrequenciesByTriangulationPointIsEqualToSomething() throws Exception {
        TriangulationPoint triangulationPoint;
        if (TestUtil.findAll(em, TriangulationPoint.class).isEmpty()) {
            frequencyRepository.saveAndFlush(frequency);
            triangulationPoint = TriangulationPointResourceIT.createEntity(em);
        } else {
            triangulationPoint = TestUtil.findAll(em, TriangulationPoint.class).get(0);
        }
        em.persist(triangulationPoint);
        em.flush();
        frequency.addTriangulationPoint(triangulationPoint);
        frequencyRepository.saveAndFlush(frequency);
        Long triangulationPointId = triangulationPoint.getId();

        // Get all the frequencyList where triangulationPoint equals to triangulationPointId
        defaultFrequencyShouldBeFound("triangulationPointId.equals=" + triangulationPointId);

        // Get all the frequencyList where triangulationPoint equals to (triangulationPointId + 1)
        defaultFrequencyShouldNotBeFound("triangulationPointId.equals=" + (triangulationPointId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultFrequencyShouldBeFound(String filter) throws Exception {
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(frequency.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));

        // Check, that the count call also returns 1
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultFrequencyShouldNotBeFound(String filter) throws Exception {
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restFrequencyMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingFrequency() throws Exception {
        // Get the frequency
        restFrequencyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFrequency() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();

        // Update the frequency
        Frequency updatedFrequency = frequencyRepository.findById(frequency.getId()).get();
        // Disconnect from session so that the updates on updatedFrequency are not directly saved in db
        em.detach(updatedFrequency);
        updatedFrequency.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restFrequencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFrequency.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFrequency))
            )
            .andExpect(status().isOk());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFrequency.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, frequency.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(frequency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(frequency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(frequency)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFrequencyWithPatch() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();

        // Update the frequency using partial update
        Frequency partialUpdatedFrequency = new Frequency();
        partialUpdatedFrequency.setId(frequency.getId());

        partialUpdatedFrequency.name(UPDATED_NAME);

        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFrequency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFrequency))
            )
            .andExpect(status().isOk());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFrequency.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateFrequencyWithPatch() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();

        // Update the frequency using partial update
        Frequency partialUpdatedFrequency = new Frequency();
        partialUpdatedFrequency.setId(frequency.getId());

        partialUpdatedFrequency.name(UPDATED_NAME).description(UPDATED_DESCRIPTION);

        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFrequency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFrequency))
            )
            .andExpect(status().isOk());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
        Frequency testFrequency = frequencyList.get(frequencyList.size() - 1);
        assertThat(testFrequency.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testFrequency.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, frequency.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(frequency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(frequency))
            )
            .andExpect(status().isBadRequest());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFrequency() throws Exception {
        int databaseSizeBeforeUpdate = frequencyRepository.findAll().size();
        frequency.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFrequencyMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(frequency))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Frequency in the database
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFrequency() throws Exception {
        // Initialize the database
        frequencyRepository.saveAndFlush(frequency);

        int databaseSizeBeforeDelete = frequencyRepository.findAll().size();

        // Delete the frequency
        restFrequencyMockMvc
            .perform(delete(ENTITY_API_URL_ID, frequency.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Frequency> frequencyList = frequencyRepository.findAll();
        assertThat(frequencyList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
