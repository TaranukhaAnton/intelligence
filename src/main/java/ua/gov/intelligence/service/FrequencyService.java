package ua.gov.intelligence.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.gov.intelligence.domain.Frequency;

/**
 * Service Interface for managing {@link Frequency}.
 */
public interface FrequencyService {
    /**
     * Save a frequency.
     *
     * @param frequency the entity to save.
     * @return the persisted entity.
     */
    Frequency save(Frequency frequency);

    /**
     * Updates a frequency.
     *
     * @param frequency the entity to update.
     * @return the persisted entity.
     */
    Frequency update(Frequency frequency);

    /**
     * Partially updates a frequency.
     *
     * @param frequency the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Frequency> partialUpdate(Frequency frequency);

    /**
     * Get all the frequencies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Frequency> findAll(Pageable pageable);

    /**
     * Get the "id" frequency.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Frequency> findOne(Long id);

    /**
     * Delete the "id" frequency.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
