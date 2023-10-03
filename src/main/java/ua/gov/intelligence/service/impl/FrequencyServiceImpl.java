package ua.gov.intelligence.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.gov.intelligence.domain.Frequency;
import ua.gov.intelligence.repository.FrequencyRepository;
import ua.gov.intelligence.service.FrequencyService;

/**
 * Service Implementation for managing {@link Frequency}.
 */
@Service
@Transactional
public class FrequencyServiceImpl implements FrequencyService {

    private final Logger log = LoggerFactory.getLogger(FrequencyServiceImpl.class);

    private final FrequencyRepository frequencyRepository;

    public FrequencyServiceImpl(FrequencyRepository frequencyRepository) {
        this.frequencyRepository = frequencyRepository;
    }

    @Override
    public Frequency save(Frequency frequency) {
        log.debug("Request to save Frequency : {}", frequency);
        return frequencyRepository.save(frequency);
    }

    @Override
    public Frequency update(Frequency frequency) {
        log.debug("Request to update Frequency : {}", frequency);
        return frequencyRepository.save(frequency);
    }

    @Override
    public Optional<Frequency> partialUpdate(Frequency frequency) {
        log.debug("Request to partially update Frequency : {}", frequency);

        return frequencyRepository
            .findById(frequency.getId())
            .map(existingFrequency -> {
                if (frequency.getName() != null) {
                    existingFrequency.setName(frequency.getName());
                }
                if (frequency.getDescription() != null) {
                    existingFrequency.setDescription(frequency.getDescription());
                }

                return existingFrequency;
            })
            .map(frequencyRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Frequency> findAll(Pageable pageable) {
        log.debug("Request to get all Frequencies");
        return frequencyRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Frequency> findOne(Long id) {
        log.debug("Request to get Frequency : {}", id);
        return frequencyRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Frequency : {}", id);
        frequencyRepository.deleteById(id);
    }
}
