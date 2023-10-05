package ua.gov.intelligence.domain;

import static javax.persistence.CascadeType.ALL;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TriangulationPoint.
 */
@Entity
@Table(name = "triangulation_point")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TriangulationPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "description")
    private String description;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "360")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "360")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @Column(name = "date", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private ZonedDateTime date;

    @ManyToOne
    @JsonIgnoreProperties(value = { "points" }, allowSetters = true)
    private TriangulationReport triangulationReport;

    @ManyToOne(cascade = ALL)
    @JsonIgnoreProperties(value = { "triangulationPoints" }, allowSetters = true)
    private Frequency frequency;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TriangulationPoint id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public TriangulationPoint description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public TriangulationPoint longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public TriangulationPoint latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public ZonedDateTime getDate() {
        return this.date;
    }

    public TriangulationPoint date(ZonedDateTime date) {
        this.setDate(date);
        return this;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public TriangulationReport getTriangulationReport() {
        return this.triangulationReport;
    }

    public void setTriangulationReport(TriangulationReport triangulationReport) {
        this.triangulationReport = triangulationReport;
    }

    public TriangulationPoint triangulationReport(TriangulationReport triangulationReport) {
        this.setTriangulationReport(triangulationReport);
        return this;
    }

    public Frequency getFrequency() {
        return this.frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public TriangulationPoint frequency(Frequency frequency) {
        this.setFrequency(frequency);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TriangulationPoint)) {
            return false;
        }
        return id != null && id.equals(((TriangulationPoint) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TriangulationPoint{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", longitude=" + getLongitude() +
            ", latitude=" + getLatitude() +
            ", date='" + getDate() + "'" +
            "}";
    }
}
