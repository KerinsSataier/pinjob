package ru.pinjob.web.rest;

import ru.pinjob.PinjobApp;
import ru.pinjob.domain.Announcement;
import ru.pinjob.repository.AnnouncementRepository;
import ru.pinjob.repository.search.AnnouncementSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AnnouncementResource REST controller.
 *
 * @see AnnouncementResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PinjobApp.class)
public class AnnouncementResourceIntTest {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("Z"));
    private static final String DEFAULT_TITLE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_BODY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_DATE_STR = dateTimeFormatter.format(DEFAULT_DATE);
    private static final String DEFAULT_PLACE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_PLACE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    private static final Integer DEFAULT_BETS = 1;
    private static final Integer UPDATED_BETS = 2;

    private static final ZonedDateTime DEFAULT_EXPIRATION = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneId.systemDefault());
    private static final ZonedDateTime UPDATED_EXPIRATION = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final String DEFAULT_EXPIRATION_STR = dateTimeFormatter.format(DEFAULT_EXPIRATION);
    private static final String DEFAULT_PICTURE = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_PICTURE = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";
    private static final String DEFAULT_PICTURES = "AAAAA";
    private static final String UPDATED_PICTURES = "BBBBB";
    private static final String DEFAULT_CITY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private AnnouncementRepository announcementRepository;

    @Inject
    private AnnouncementSearchRepository announcementSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restAnnouncementMockMvc;

    private Announcement announcement;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AnnouncementResource announcementResource = new AnnouncementResource();
        ReflectionTestUtils.setField(announcementResource, "announcementSearchRepository", announcementSearchRepository);
        ReflectionTestUtils.setField(announcementResource, "announcementRepository", announcementRepository);
        this.restAnnouncementMockMvc = MockMvcBuilders.standaloneSetup(announcementResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Announcement createEntity(EntityManager em) {
        Announcement announcement = new Announcement();
        announcement = new Announcement()
                .title(DEFAULT_TITLE)
                .body(DEFAULT_BODY)
                .date(DEFAULT_DATE)
                .place(DEFAULT_PLACE)
                .price(DEFAULT_PRICE)
                .bets(DEFAULT_BETS)
                .expiration(DEFAULT_EXPIRATION)
                .picture(DEFAULT_PICTURE)
                .pictures(DEFAULT_PICTURES)
                .city(DEFAULT_CITY);
        return announcement;
    }

    @Before
    public void initTest() {
        announcementSearchRepository.deleteAll();
        announcement = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnnouncement() throws Exception {
        int databaseSizeBeforeCreate = announcementRepository.findAll().size();

        // Create the Announcement

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isCreated());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeCreate + 1);
        Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testAnnouncement.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testAnnouncement.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testAnnouncement.getPlace()).isEqualTo(DEFAULT_PLACE);
        assertThat(testAnnouncement.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testAnnouncement.getBets()).isEqualTo(DEFAULT_BETS);
        assertThat(testAnnouncement.getExpiration()).isEqualTo(DEFAULT_EXPIRATION);
        assertThat(testAnnouncement.getPicture()).isEqualTo(DEFAULT_PICTURE);
        assertThat(testAnnouncement.getPictures()).isEqualTo(DEFAULT_PICTURES);
        assertThat(testAnnouncement.getCity()).isEqualTo(DEFAULT_CITY);

        // Validate the Announcement in ElasticSearch
        Announcement announcementEs = announcementSearchRepository.findOne(testAnnouncement.getId());
        assertThat(announcementEs).isEqualToComparingFieldByField(testAnnouncement);
    }

    @Test
    @Transactional
    public void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setTitle(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkBodyIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setBody(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = announcementRepository.findAll().size();
        // set the field null
        announcement.setDate(null);

        // Create the Announcement, which fails.

        restAnnouncementMockMvc.perform(post("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(announcement)))
                .andExpect(status().isBadRequest());

        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAnnouncements() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get all the announcements
        restAnnouncementMockMvc.perform(get("/api/announcements?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)))
                .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
                .andExpect(jsonPath("$.[*].bets").value(hasItem(DEFAULT_BETS)))
                .andExpect(jsonPath("$.[*].expiration").value(hasItem(DEFAULT_EXPIRATION_STR)))
                .andExpect(jsonPath("$.[*].picture").value(hasItem(DEFAULT_PICTURE.toString())))
                .andExpect(jsonPath("$.[*].pictures").value(hasItem(DEFAULT_PICTURES.toString())))
                .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())));
    }

    @Test
    @Transactional
    public void getAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);

        // Get the announcement
        restAnnouncementMockMvc.perform(get("/api/announcements/{id}", announcement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(announcement.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE_STR))
            .andExpect(jsonPath("$.place").value(DEFAULT_PLACE.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE))
            .andExpect(jsonPath("$.bets").value(DEFAULT_BETS))
            .andExpect(jsonPath("$.expiration").value(DEFAULT_EXPIRATION_STR))
            .andExpect(jsonPath("$.picture").value(DEFAULT_PICTURE.toString()))
            .andExpect(jsonPath("$.pictures").value(DEFAULT_PICTURES.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingAnnouncement() throws Exception {
        // Get the announcement
        restAnnouncementMockMvc.perform(get("/api/announcements/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);
        announcementSearchRepository.save(announcement);
        int databaseSizeBeforeUpdate = announcementRepository.findAll().size();

        // Update the announcement
        Announcement updatedAnnouncement = announcementRepository.findOne(announcement.getId());
        updatedAnnouncement
                .title(UPDATED_TITLE)
                .body(UPDATED_BODY)
                .date(UPDATED_DATE)
                .place(UPDATED_PLACE)
                .price(UPDATED_PRICE)
                .bets(UPDATED_BETS)
                .expiration(UPDATED_EXPIRATION)
                .picture(UPDATED_PICTURE)
                .pictures(UPDATED_PICTURES)
                .city(UPDATED_CITY);

        restAnnouncementMockMvc.perform(put("/api/announcements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedAnnouncement)))
                .andExpect(status().isOk());

        // Validate the Announcement in the database
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeUpdate);
        Announcement testAnnouncement = announcements.get(announcements.size() - 1);
        assertThat(testAnnouncement.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAnnouncement.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testAnnouncement.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testAnnouncement.getPlace()).isEqualTo(UPDATED_PLACE);
        assertThat(testAnnouncement.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testAnnouncement.getBets()).isEqualTo(UPDATED_BETS);
        assertThat(testAnnouncement.getExpiration()).isEqualTo(UPDATED_EXPIRATION);
        assertThat(testAnnouncement.getPicture()).isEqualTo(UPDATED_PICTURE);
        assertThat(testAnnouncement.getPictures()).isEqualTo(UPDATED_PICTURES);
        assertThat(testAnnouncement.getCity()).isEqualTo(UPDATED_CITY);

        // Validate the Announcement in ElasticSearch
        Announcement announcementEs = announcementSearchRepository.findOne(testAnnouncement.getId());
        assertThat(announcementEs).isEqualToComparingFieldByField(testAnnouncement);
    }

    @Test
    @Transactional
    public void deleteAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);
        announcementSearchRepository.save(announcement);
        int databaseSizeBeforeDelete = announcementRepository.findAll().size();

        // Get the announcement
        restAnnouncementMockMvc.perform(delete("/api/announcements/{id}", announcement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean announcementExistsInEs = announcementSearchRepository.exists(announcement.getId());
        assertThat(announcementExistsInEs).isFalse();

        // Validate the database is empty
        List<Announcement> announcements = announcementRepository.findAll();
        assertThat(announcements).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchAnnouncement() throws Exception {
        // Initialize the database
        announcementRepository.saveAndFlush(announcement);
        announcementSearchRepository.save(announcement);

        // Search the announcement
        restAnnouncementMockMvc.perform(get("/api/_search/announcements?query=id:" + announcement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(announcement.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE_STR)))
            .andExpect(jsonPath("$.[*].place").value(hasItem(DEFAULT_PLACE.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.[*].bets").value(hasItem(DEFAULT_BETS)))
            .andExpect(jsonPath("$.[*].expiration").value(hasItem(DEFAULT_EXPIRATION_STR)))
            .andExpect(jsonPath("$.[*].picture").value(hasItem(DEFAULT_PICTURE.toString())))
            .andExpect(jsonPath("$.[*].pictures").value(hasItem(DEFAULT_PICTURES.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())));
    }
}
