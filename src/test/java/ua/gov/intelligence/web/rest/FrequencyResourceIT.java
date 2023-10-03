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
import ua.gov.intelligence.repository.FrequencyRepository;

/**
 * Integration tests for the {@link FrequencyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FrequencyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

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
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
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
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
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
