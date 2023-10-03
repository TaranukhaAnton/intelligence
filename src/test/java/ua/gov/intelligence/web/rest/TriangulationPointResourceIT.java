package ua.gov.intelligence.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ua.gov.intelligence.web.rest.TestUtil.sameInstant;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ua.gov.intelligence.IntegrationTest;
import ua.gov.intelligence.domain.Frequency;
import ua.gov.intelligence.domain.TriangulationPoint;
import ua.gov.intelligence.domain.TriangulationReport;
import ua.gov.intelligence.repository.TriangulationPointRepository;
import ua.gov.intelligence.service.criteria.TriangulationPointCriteria;

/**
 * Integration tests for the {@link TriangulationPointResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TriangulationPointResourceIT {

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Double DEFAULT_LONGITUDE = 0D;
    private static final Double UPDATED_LONGITUDE = 1D;
    private static final Double SMALLER_LONGITUDE = 0D - 1D;

    private static final Double DEFAULT_LATITUDE = 0D;
    private static final Double UPDATED_LATITUDE = 1D;
    private static final Double SMALLER_LATITUDE = 0D - 1D;

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final String ENTITY_API_URL = "/api/triangulation-points";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TriangulationPointRepository triangulationPointRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTriangulationPointMockMvc;

    private TriangulationPoint triangulationPoint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TriangulationPoint createEntity(EntityManager em) {
        TriangulationPoint triangulationPoint = new TriangulationPoint()
            .description(DEFAULT_DESCRIPTION)
            .longitude(DEFAULT_LONGITUDE)
            .latitude(DEFAULT_LATITUDE)
            .date(DEFAULT_DATE);
        return triangulationPoint;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TriangulationPoint createUpdatedEntity(EntityManager em) {
        TriangulationPoint triangulationPoint = new TriangulationPoint()
            .description(UPDATED_DESCRIPTION)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .date(UPDATED_DATE);
        return triangulationPoint;
    }

    @BeforeEach
    public void initTest() {
        triangulationPoint = createEntity(em);
    }

    @Test
    @Transactional
    void createTriangulationPoint() throws Exception {
        int databaseSizeBeforeCreate = triangulationPointRepository.findAll().size();
        // Create the TriangulationPoint
        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isCreated());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeCreate + 1);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(DEFAULT_LATITUDE);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    @Transactional
    void createTriangulationPointWithExistingId() throws Exception {
        // Create the TriangulationPoint with an existing ID
        triangulationPoint.setId(1L);

        int databaseSizeBeforeCreate = triangulationPointRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = triangulationPointRepository.findAll().size();
        // set the field null
        triangulationPoint.setLongitude(null);

        // Create the TriangulationPoint, which fails.

        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        int databaseSizeBeforeTest = triangulationPointRepository.findAll().size();
        // set the field null
        triangulationPoint.setLatitude(null);

        // Create the TriangulationPoint, which fails.

        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = triangulationPointRepository.findAll().size();
        // set the field null
        triangulationPoint.setDate(null);

        // Create the TriangulationPoint, which fails.

        restTriangulationPointMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTriangulationPoints() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(triangulationPoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));
    }

    @Test
    @Transactional
    void getTriangulationPoint() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get the triangulationPoint
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL_ID, triangulationPoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(triangulationPoint.getId().intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE.doubleValue()))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE.doubleValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)));
    }

    @Test
    @Transactional
    void getTriangulationPointsByIdFiltering() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        Long id = triangulationPoint.getId();

        defaultTriangulationPointShouldBeFound("id.equals=" + id);
        defaultTriangulationPointShouldNotBeFound("id.notEquals=" + id);

        defaultTriangulationPointShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTriangulationPointShouldNotBeFound("id.greaterThan=" + id);

        defaultTriangulationPointShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTriangulationPointShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where description equals to DEFAULT_DESCRIPTION
        defaultTriangulationPointShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the triangulationPointList where description equals to UPDATED_DESCRIPTION
        defaultTriangulationPointShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultTriangulationPointShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the triangulationPointList where description equals to UPDATED_DESCRIPTION
        defaultTriangulationPointShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where description is not null
        defaultTriangulationPointShouldBeFound("description.specified=true");

        // Get all the triangulationPointList where description is null
        defaultTriangulationPointShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDescriptionContainsSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where description contains DEFAULT_DESCRIPTION
        defaultTriangulationPointShouldBeFound("description.contains=" + DEFAULT_DESCRIPTION);

        // Get all the triangulationPointList where description contains UPDATED_DESCRIPTION
        defaultTriangulationPointShouldNotBeFound("description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDescriptionNotContainsSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where description does not contain DEFAULT_DESCRIPTION
        defaultTriangulationPointShouldNotBeFound("description.doesNotContain=" + DEFAULT_DESCRIPTION);

        // Get all the triangulationPointList where description does not contain UPDATED_DESCRIPTION
        defaultTriangulationPointShouldBeFound("description.doesNotContain=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude equals to DEFAULT_LONGITUDE
        defaultTriangulationPointShouldBeFound("longitude.equals=" + DEFAULT_LONGITUDE);

        // Get all the triangulationPointList where longitude equals to UPDATED_LONGITUDE
        defaultTriangulationPointShouldNotBeFound("longitude.equals=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsInShouldWork() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude in DEFAULT_LONGITUDE or UPDATED_LONGITUDE
        defaultTriangulationPointShouldBeFound("longitude.in=" + DEFAULT_LONGITUDE + "," + UPDATED_LONGITUDE);

        // Get all the triangulationPointList where longitude equals to UPDATED_LONGITUDE
        defaultTriangulationPointShouldNotBeFound("longitude.in=" + UPDATED_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude is not null
        defaultTriangulationPointShouldBeFound("longitude.specified=true");

        // Get all the triangulationPointList where longitude is null
        defaultTriangulationPointShouldNotBeFound("longitude.specified=false");
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude is greater than or equal to DEFAULT_LONGITUDE
        defaultTriangulationPointShouldBeFound("longitude.greaterThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the triangulationPointList where longitude is greater than or equal to (DEFAULT_LONGITUDE + 1)
        defaultTriangulationPointShouldNotBeFound("longitude.greaterThanOrEqual=" + (DEFAULT_LONGITUDE + 1));
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude is less than or equal to DEFAULT_LONGITUDE
        defaultTriangulationPointShouldBeFound("longitude.lessThanOrEqual=" + DEFAULT_LONGITUDE);

        // Get all the triangulationPointList where longitude is less than or equal to SMALLER_LONGITUDE
        defaultTriangulationPointShouldNotBeFound("longitude.lessThanOrEqual=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude is less than DEFAULT_LONGITUDE
        defaultTriangulationPointShouldNotBeFound("longitude.lessThan=" + DEFAULT_LONGITUDE);

        // Get all the triangulationPointList where longitude is less than (DEFAULT_LONGITUDE + 1)
        defaultTriangulationPointShouldBeFound("longitude.lessThan=" + (DEFAULT_LONGITUDE + 1));
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLongitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where longitude is greater than DEFAULT_LONGITUDE
        defaultTriangulationPointShouldNotBeFound("longitude.greaterThan=" + DEFAULT_LONGITUDE);

        // Get all the triangulationPointList where longitude is greater than SMALLER_LONGITUDE
        defaultTriangulationPointShouldBeFound("longitude.greaterThan=" + SMALLER_LONGITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude equals to DEFAULT_LATITUDE
        defaultTriangulationPointShouldBeFound("latitude.equals=" + DEFAULT_LATITUDE);

        // Get all the triangulationPointList where latitude equals to UPDATED_LATITUDE
        defaultTriangulationPointShouldNotBeFound("latitude.equals=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsInShouldWork() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude in DEFAULT_LATITUDE or UPDATED_LATITUDE
        defaultTriangulationPointShouldBeFound("latitude.in=" + DEFAULT_LATITUDE + "," + UPDATED_LATITUDE);

        // Get all the triangulationPointList where latitude equals to UPDATED_LATITUDE
        defaultTriangulationPointShouldNotBeFound("latitude.in=" + UPDATED_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsNullOrNotNull() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude is not null
        defaultTriangulationPointShouldBeFound("latitude.specified=true");

        // Get all the triangulationPointList where latitude is null
        defaultTriangulationPointShouldNotBeFound("latitude.specified=false");
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude is greater than or equal to DEFAULT_LATITUDE
        defaultTriangulationPointShouldBeFound("latitude.greaterThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the triangulationPointList where latitude is greater than or equal to (DEFAULT_LATITUDE + 1)
        defaultTriangulationPointShouldNotBeFound("latitude.greaterThanOrEqual=" + (DEFAULT_LATITUDE + 1));
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude is less than or equal to DEFAULT_LATITUDE
        defaultTriangulationPointShouldBeFound("latitude.lessThanOrEqual=" + DEFAULT_LATITUDE);

        // Get all the triangulationPointList where latitude is less than or equal to SMALLER_LATITUDE
        defaultTriangulationPointShouldNotBeFound("latitude.lessThanOrEqual=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsLessThanSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude is less than DEFAULT_LATITUDE
        defaultTriangulationPointShouldNotBeFound("latitude.lessThan=" + DEFAULT_LATITUDE);

        // Get all the triangulationPointList where latitude is less than (DEFAULT_LATITUDE + 1)
        defaultTriangulationPointShouldBeFound("latitude.lessThan=" + (DEFAULT_LATITUDE + 1));
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByLatitudeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where latitude is greater than DEFAULT_LATITUDE
        defaultTriangulationPointShouldNotBeFound("latitude.greaterThan=" + DEFAULT_LATITUDE);

        // Get all the triangulationPointList where latitude is greater than SMALLER_LATITUDE
        defaultTriangulationPointShouldBeFound("latitude.greaterThan=" + SMALLER_LATITUDE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date equals to DEFAULT_DATE
        defaultTriangulationPointShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the triangulationPointList where date equals to UPDATED_DATE
        defaultTriangulationPointShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date in DEFAULT_DATE or UPDATED_DATE
        defaultTriangulationPointShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the triangulationPointList where date equals to UPDATED_DATE
        defaultTriangulationPointShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date is not null
        defaultTriangulationPointShouldBeFound("date.specified=true");

        // Get all the triangulationPointList where date is null
        defaultTriangulationPointShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date is greater than or equal to DEFAULT_DATE
        defaultTriangulationPointShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the triangulationPointList where date is greater than or equal to UPDATED_DATE
        defaultTriangulationPointShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date is less than or equal to DEFAULT_DATE
        defaultTriangulationPointShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the triangulationPointList where date is less than or equal to SMALLER_DATE
        defaultTriangulationPointShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date is less than DEFAULT_DATE
        defaultTriangulationPointShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the triangulationPointList where date is less than UPDATED_DATE
        defaultTriangulationPointShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        // Get all the triangulationPointList where date is greater than DEFAULT_DATE
        defaultTriangulationPointShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the triangulationPointList where date is greater than SMALLER_DATE
        defaultTriangulationPointShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByTriangulationReportIsEqualToSomething() throws Exception {
        TriangulationReport triangulationReport;
        if (TestUtil.findAll(em, TriangulationReport.class).isEmpty()) {
            triangulationPointRepository.saveAndFlush(triangulationPoint);
            triangulationReport = TriangulationReportResourceIT.createEntity(em);
        } else {
            triangulationReport = TestUtil.findAll(em, TriangulationReport.class).get(0);
        }
        em.persist(triangulationReport);
        em.flush();
        triangulationPoint.setTriangulationReport(triangulationReport);
        triangulationPointRepository.saveAndFlush(triangulationPoint);
        Long triangulationReportId = triangulationReport.getId();

        // Get all the triangulationPointList where triangulationReport equals to triangulationReportId
        defaultTriangulationPointShouldBeFound("triangulationReportId.equals=" + triangulationReportId);

        // Get all the triangulationPointList where triangulationReport equals to (triangulationReportId + 1)
        defaultTriangulationPointShouldNotBeFound("triangulationReportId.equals=" + (triangulationReportId + 1));
    }

    @Test
    @Transactional
    void getAllTriangulationPointsByFrequencyIsEqualToSomething() throws Exception {
        Frequency frequency;
        if (TestUtil.findAll(em, Frequency.class).isEmpty()) {
            triangulationPointRepository.saveAndFlush(triangulationPoint);
            frequency = FrequencyResourceIT.createEntity(em);
        } else {
            frequency = TestUtil.findAll(em, Frequency.class).get(0);
        }
        em.persist(frequency);
        em.flush();
        triangulationPoint.setFrequency(frequency);
        triangulationPointRepository.saveAndFlush(triangulationPoint);
        Long frequencyId = frequency.getId();

        // Get all the triangulationPointList where frequency equals to frequencyId
        defaultTriangulationPointShouldBeFound("frequencyId.equals=" + frequencyId);

        // Get all the triangulationPointList where frequency equals to (frequencyId + 1)
        defaultTriangulationPointShouldNotBeFound("frequencyId.equals=" + (frequencyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTriangulationPointShouldBeFound(String filter) throws Exception {
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(triangulationPoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))));

        // Check, that the count call also returns 1
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTriangulationPointShouldNotBeFound(String filter) throws Exception {
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTriangulationPointMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTriangulationPoint() throws Exception {
        // Get the triangulationPoint
        restTriangulationPointMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTriangulationPoint() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();

        // Update the triangulationPoint
        TriangulationPoint updatedTriangulationPoint = triangulationPointRepository.findById(triangulationPoint.getId()).get();
        // Disconnect from session so that the updates on updatedTriangulationPoint are not directly saved in db
        em.detach(updatedTriangulationPoint);
        updatedTriangulationPoint
            .description(UPDATED_DESCRIPTION)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .date(UPDATED_DATE);

        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTriangulationPoint.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTriangulationPoint))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, triangulationPoint.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTriangulationPointWithPatch() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();

        // Update the triangulationPoint using partial update
        TriangulationPoint partialUpdatedTriangulationPoint = new TriangulationPoint();
        partialUpdatedTriangulationPoint.setId(triangulationPoint.getId());

        partialUpdatedTriangulationPoint.latitude(UPDATED_LATITUDE).date(UPDATED_DATE);

        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTriangulationPoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTriangulationPoint))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(DEFAULT_LONGITUDE);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateTriangulationPointWithPatch() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();

        // Update the triangulationPoint using partial update
        TriangulationPoint partialUpdatedTriangulationPoint = new TriangulationPoint();
        partialUpdatedTriangulationPoint.setId(triangulationPoint.getId());

        partialUpdatedTriangulationPoint
            .description(UPDATED_DESCRIPTION)
            .longitude(UPDATED_LONGITUDE)
            .latitude(UPDATED_LATITUDE)
            .date(UPDATED_DATE);

        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTriangulationPoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTriangulationPoint))
            )
            .andExpect(status().isOk());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
        TriangulationPoint testTriangulationPoint = triangulationPointList.get(triangulationPointList.size() - 1);
        assertThat(testTriangulationPoint.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTriangulationPoint.getLongitude()).isEqualTo(UPDATED_LONGITUDE);
        assertThat(testTriangulationPoint.getLatitude()).isEqualTo(UPDATED_LATITUDE);
        assertThat(testTriangulationPoint.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, triangulationPoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTriangulationPoint() throws Exception {
        int databaseSizeBeforeUpdate = triangulationPointRepository.findAll().size();
        triangulationPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTriangulationPointMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(triangulationPoint))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TriangulationPoint in the database
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTriangulationPoint() throws Exception {
        // Initialize the database
        triangulationPointRepository.saveAndFlush(triangulationPoint);

        int databaseSizeBeforeDelete = triangulationPointRepository.findAll().size();

        // Delete the triangulationPoint
        restTriangulationPointMockMvc
            .perform(delete(ENTITY_API_URL_ID, triangulationPoint.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TriangulationPoint> triangulationPointList = triangulationPointRepository.findAll();
        assertThat(triangulationPointList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
