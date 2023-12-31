package ua.gov.intelligence.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link ua.gov.intelligence.domain.Frequency} entity. This class is used
 * in {@link ua.gov.intelligence.web.rest.FrequencyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /frequencies?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FrequencyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private DoubleFilter name;

    private StringFilter description;

    private LongFilter triangulationPointId;

    private Boolean distinct;

    public FrequencyCriteria() {}

    public FrequencyCriteria(FrequencyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.triangulationPointId = other.triangulationPointId == null ? null : other.triangulationPointId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public FrequencyCriteria copy() {
        return new FrequencyCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public DoubleFilter getName() {
        return name;
    }

    public DoubleFilter name() {
        if (name == null) {
            name = new DoubleFilter();
        }
        return name;
    }

    public void setName(DoubleFilter name) {
        this.name = name;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getTriangulationPointId() {
        return triangulationPointId;
    }

    public LongFilter triangulationPointId() {
        if (triangulationPointId == null) {
            triangulationPointId = new LongFilter();
        }
        return triangulationPointId;
    }

    public void setTriangulationPointId(LongFilter triangulationPointId) {
        this.triangulationPointId = triangulationPointId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FrequencyCriteria that = (FrequencyCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(triangulationPointId, that.triangulationPointId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, triangulationPointId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FrequencyCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (triangulationPointId != null ? "triangulationPointId=" + triangulationPointId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
