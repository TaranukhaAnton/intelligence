package ua.gov.intelligence.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ua.gov.intelligence.domain.TriangulationPoint;

/**
 * Spring Data JPA repository for the TriangulationPoint entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TriangulationPointRepository
    extends JpaRepository<TriangulationPoint, Long>, JpaSpecificationExecutor<TriangulationPoint> {}
