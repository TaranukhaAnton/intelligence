package ua.gov.intelligence.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ua.gov.intelligence.domain.TriangulationReport;

/**
 * Spring Data JPA repository for the TriangulationReport entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TriangulationReportRepository extends JpaRepository<TriangulationReport, Long> {}
