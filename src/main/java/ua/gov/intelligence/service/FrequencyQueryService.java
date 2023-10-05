package ua.gov.intelligence.service;

import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import ua.gov.intelligence.domain.Frequency;
import ua.gov.intelligence.domain.Frequency_;
import ua.gov.intelligence.domain.TriangulationPoint_;
import ua.gov.intelligence.repository.FrequencyRepository;
import ua.gov.intelligence.service.criteria.FrequencyCriteria;

/**
 * Service for executing complex queries for {@link Frequency} entities in the database.
 * The main input is a {@link FrequencyCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link Frequency} or a {@link Page} of {@link Frequency} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class FrequencyQueryService extends QueryService<Frequency> {

    private final Logger log = LoggerFactory.getLogger(FrequencyQueryService.class);

    private final FrequencyRepository frequencyRepository;

    public FrequencyQueryService(FrequencyRepository frequencyRepository) {
        this.frequencyRepository = frequencyRepository;
    }

    /**
     * Return a {@link List} of {@link Frequency} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<Frequency> findByCriteria(FrequencyCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Frequency> specification = createSpecification(criteria);
        return frequencyRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link Frequency} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<Frequency> findByCriteria(FrequencyCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Frequency> specification = createSpecification(criteria);
        return frequencyRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(FrequencyCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Frequency> specification = createSpecification(criteria);
        return frequencyRepository.count(specification);
    }

    /**
     * Function to convert {@link FrequencyCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Frequency> createSpecification(FrequencyCriteria criteria) {
        Specification<Frequency> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Frequency_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getName(), Frequency_.name));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Frequency_.description));
            }
            if (criteria.getTriangulationPointId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTriangulationPointId(),
                            root -> root.join(Frequency_.triangulationPoints, JoinType.LEFT).get(TriangulationPoint_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
