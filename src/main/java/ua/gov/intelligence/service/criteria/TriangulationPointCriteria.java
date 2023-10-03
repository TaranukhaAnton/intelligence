package ua.gov.intelligence.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link ua.gov.intelligence.domain.TriangulationPoint} entity. This class is used
 * in {@link ua.gov.intelligence.web.rest.TriangulationPointResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /triangulation-points?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TriangulationPointCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter description;

    private DoubleFilter longitude;

    private DoubleFilter latitude;

    private ZonedDateTimeFilter date;

    private LongFilter triangulationReportId;

    private LongFilter frequencyId;

    private Boolean distinct;

    public TriangulationPointCriteria() {}

    public TriangulationPointCriteria(TriangulationPointCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.longitude = other.longitude == null ? null : other.longitude.copy();
        this.latitude = other.latitude == null ? null : other.latitude.copy();
        this.date = other.date == null ? null : other.date.copy();
        this.triangulationReportId = other.triangulationReportId == null ? null : other.triangulationReportId.copy();
        this.frequencyId = other.frequencyId == null ? null : other.frequencyId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TriangulationPointCriteria copy() {
        return new TriangulationPointCriteria(this);
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

    public DoubleFilter getLongitude() {
        return longitude;
    }

    public DoubleFilter longitude() {
        if (longitude == null) {
            longitude = new DoubleFilter();
        }
        return longitude;
    }

    public void setLongitude(DoubleFilter longitude) {
        this.longitude = longitude;
    }

    public DoubleFilter getLatitude() {
        return latitude;
    }

    public DoubleFilter latitude() {
        if (latitude == null) {
            latitude = new DoubleFilter();
        }
        return latitude;
    }

    public void setLatitude(DoubleFilter latitude) {
        this.latitude = latitude;
    }

    public ZonedDateTimeFilter getDate() {
        return date;
    }

    public ZonedDateTimeFilter date() {
        if (date == null) {
            date = new ZonedDateTimeFilter();
        }
        return date;
    }

    public void setDate(ZonedDateTimeFilter date) {
        this.date = date;
    }

    public LongFilter getTriangulationReportId() {
        return triangulationReportId;
    }

    public LongFilter triangulationReportId() {
        if (triangulationReportId == null) {
            triangulationReportId = new LongFilter();
        }
        return triangulationReportId;
    }

    public void setTriangulationReportId(LongFilter triangulationReportId) {
        this.triangulationReportId = triangulationReportId;
    }

    public LongFilter getFrequencyId() {
        return frequencyId;
    }

    public LongFilter frequencyId() {
        if (frequencyId == null) {
            frequencyId = new LongFilter();
        }
        return frequencyId;
    }

    public void setFrequencyId(LongFilter frequencyId) {
        this.frequencyId = frequencyId;
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
        final TriangulationPointCriteria that = (TriangulationPointCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(description, that.description) &&
            Objects.equals(longitude, that.longitude) &&
            Objects.equals(latitude, that.latitude) &&
            Objects.equals(date, that.date) &&
            Objects.equals(triangulationReportId, that.triangulationReportId) &&
            Objects.equals(frequencyId, that.frequencyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, longitude, latitude, date, triangulationReportId, frequencyId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TriangulationPointCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (longitude != null ? "longitude=" + longitude + ", " : "") +
            (latitude != null ? "latitude=" + latitude + ", " : "") +
            (date != null ? "date=" + date + ", " : "") +
            (triangulationReportId != null ? "triangulationReportId=" + triangulationReportId + ", " : "") +
            (frequencyId != null ? "frequencyId=" + frequencyId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
