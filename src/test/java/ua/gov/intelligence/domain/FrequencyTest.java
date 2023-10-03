package ua.gov.intelligence.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import ua.gov.intelligence.web.rest.TestUtil;

class FrequencyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Frequency.class);
        Frequency frequency1 = new Frequency();
        frequency1.setId(1L);
        Frequency frequency2 = new Frequency();
        frequency2.setId(frequency1.getId());
        assertThat(frequency1).isEqualTo(frequency2);
        frequency2.setId(2L);
        assertThat(frequency1).isNotEqualTo(frequency2);
        frequency1.setId(null);
        assertThat(frequency1).isNotEqualTo(frequency2);
    }
}
