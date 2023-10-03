package ua.gov.intelligence.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ua.gov.intelligence.domain.Frequency;

/**
 * Spring Data JPA repository for the Frequency entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FrequencyRepository extends JpaRepository<Frequency, Long> {}
