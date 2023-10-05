package ua.gov.intelligence.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Frequency.
 */
@Entity
@Table(name = "frequency")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Frequency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private Double name;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "frequency", cascade = CascadeType.ALL)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "triangulationReport", "frequency" }, allowSetters = true)
    private Set<TriangulationPoint> triangulationPoints = new HashSet<>();

    public Frequency(Double name) {
        this.name = name;
    }

    public Frequency() {}

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Frequency id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getName() {
        return this.name;
    }

    public Frequency name(Double name) {
        this.setName(name);
        return this;
    }

    public void setName(Double name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Frequency description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<TriangulationPoint> getTriangulationPoints() {
        return this.triangulationPoints;
    }

    public void setTriangulationPoints(Set<TriangulationPoint> triangulationPoints) {
        if (this.triangulationPoints != null) {
            this.triangulationPoints.forEach(i -> i.setFrequency(null));
        }
        if (triangulationPoints != null) {
            triangulationPoints.forEach(i -> i.setFrequency(this));
        }
        this.triangulationPoints = triangulationPoints;
    }

    public Frequency triangulationPoints(Set<TriangulationPoint> triangulationPoints) {
        this.setTriangulationPoints(triangulationPoints);
        return this;
    }

    public Frequency addTriangulationPoint(TriangulationPoint triangulationPoint) {
        this.triangulationPoints.add(triangulationPoint);
        triangulationPoint.setFrequency(this);
        return this;
    }

    public Frequency removeTriangulationPoint(TriangulationPoint triangulationPoint) {
        this.triangulationPoints.remove(triangulationPoint);
        triangulationPoint.setFrequency(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Frequency)) {
            return false;
        }
        return id != null && id.equals(((Frequency) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Frequency{" +
            "id=" + getId() +
            ", name=" + getName() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
