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
import ua.gov.intelligence.domain.*; // for static metamodels
import ua.gov.intelligence.domain.TriangulationPoint;
import ua.gov.intelligence.repository.TriangulationPointRepository;
import ua.gov.intelligence.service.criteria.TriangulationPointCriteria;

/**
 * Service for executing complex queries for {@link TriangulationPoint} entities in the database.
 * The main input is a {@link TriangulationPointCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TriangulationPoint} or a {@link Page} of {@link TriangulationPoint} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TriangulationPointQueryService extends QueryService<TriangulationPoint> {

    private final Logger log = LoggerFactory.getLogger(TriangulationPointQueryService.class);

    private final TriangulationPointRepository triangulationPointRepository;

    public TriangulationPointQueryService(TriangulationPointRepository triangulationPointRepository) {
        this.triangulationPointRepository = triangulationPointRepository;
    }

    /**
     * Return a {@link List} of {@link TriangulationPoint} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TriangulationPoint> findByCriteria(TriangulationPointCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TriangulationPoint> specification = createSpecification(criteria);
        return triangulationPointRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link TriangulationPoint} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TriangulationPoint> findByCriteria(TriangulationPointCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TriangulationPoint> specification = createSpecification(criteria);
        return triangulationPointRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TriangulationPointCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TriangulationPoint> specification = createSpecification(criteria);
        return triangulationPointRepository.count(specification);
    }

    /**
     * Function to convert {@link TriangulationPointCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TriangulationPoint> createSpecification(TriangulationPointCriteria criteria) {
        Specification<TriangulationPoint> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TriangulationPoint_.id));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), TriangulationPoint_.description));
            }
            if (criteria.getLongitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLongitude(), TriangulationPoint_.longitude));
            }
            if (criteria.getLatitude() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLatitude(), TriangulationPoint_.latitude));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), TriangulationPoint_.date));
            }
            if (criteria.getTriangulationReportId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTriangulationReportId(),
                            root -> root.join(TriangulationPoint_.triangulationReport, JoinType.LEFT).get(TriangulationReport_.id)
                        )
                    );
            }
            if (criteria.getFrequencyId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getFrequencyId(),
                            root -> root.join(TriangulationPoint_.frequency, JoinType.LEFT).get(Frequency_.id)
                        )
                    );
            }
        }
        return specification;
    }
}
