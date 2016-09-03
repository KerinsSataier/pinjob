package ru.pinjob.web.rest;

import ru.pinjob.PinjobApp;
import ru.pinjob.domain.Status;
import ru.pinjob.repository.StatusRepository;
import ru.pinjob.repository.search.StatusSearchRepository;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the StatusResource REST controller.
 *
 * @see StatusResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PinjobApp.class)
public class StatusResourceIntTest {
    private static final String DEFAULT_NAME = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB";

    @Inject
    private StatusRepository statusRepository;

    @Inject
    private StatusSearchRepository statusSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restStatusMockMvc;

    private Status status;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StatusResource statusResource = new StatusResource();
        ReflectionTestUtils.setField(statusResource, "statusSearchRepository", statusSearchRepository);
        ReflectionTestUtils.setField(statusResource, "statusRepository", statusRepository);
        this.restStatusMockMvc = MockMvcBuilders.standaloneSetup(statusResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Status createEntity(EntityManager em) {
        Status status = new Status();
        status = new Status()
                .name(DEFAULT_NAME);
        return status;
    }

    @Before
    public void initTest() {
        statusSearchRepository.deleteAll();
        status = createEntity(em);
    }

    @Test
    @Transactional
    public void createStatus() throws Exception {
        int databaseSizeBeforeCreate = statusRepository.findAll().size();

        // Create the Status

        restStatusMockMvc.perform(post("/api/statuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(status)))
                .andExpect(status().isCreated());

        // Validate the Status in the database
        List<Status> statuses = statusRepository.findAll();
        assertThat(statuses).hasSize(databaseSizeBeforeCreate + 1);
        Status testStatus = statuses.get(statuses.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the Status in ElasticSearch
        Status statusEs = statusSearchRepository.findOne(testStatus.getId());
        assertThat(statusEs).isEqualToComparingFieldByField(testStatus);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = statusRepository.findAll().size();
        // set the field null
        status.setName(null);

        // Create the Status, which fails.

        restStatusMockMvc.perform(post("/api/statuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(status)))
                .andExpect(status().isBadRequest());

        List<Status> statuses = statusRepository.findAll();
        assertThat(statuses).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStatuses() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get all the statuses
        restStatusMockMvc.perform(get("/api/statuses?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);

        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(status.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStatus() throws Exception {
        // Get the status
        restStatusMockMvc.perform(get("/api/statuses/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);
        int databaseSizeBeforeUpdate = statusRepository.findAll().size();

        // Update the status
        Status updatedStatus = statusRepository.findOne(status.getId());
        updatedStatus
                .name(UPDATED_NAME);

        restStatusMockMvc.perform(put("/api/statuses")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedStatus)))
                .andExpect(status().isOk());

        // Validate the Status in the database
        List<Status> statuses = statusRepository.findAll();
        assertThat(statuses).hasSize(databaseSizeBeforeUpdate);
        Status testStatus = statuses.get(statuses.size() - 1);
        assertThat(testStatus.getName()).isEqualTo(UPDATED_NAME);

        // Validate the Status in ElasticSearch
        Status statusEs = statusSearchRepository.findOne(testStatus.getId());
        assertThat(statusEs).isEqualToComparingFieldByField(testStatus);
    }

    @Test
    @Transactional
    public void deleteStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);
        int databaseSizeBeforeDelete = statusRepository.findAll().size();

        // Get the status
        restStatusMockMvc.perform(delete("/api/statuses/{id}", status.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean statusExistsInEs = statusSearchRepository.exists(status.getId());
        assertThat(statusExistsInEs).isFalse();

        // Validate the database is empty
        List<Status> statuses = statusRepository.findAll();
        assertThat(statuses).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchStatus() throws Exception {
        // Initialize the database
        statusRepository.saveAndFlush(status);
        statusSearchRepository.save(status);

        // Search the status
        restStatusMockMvc.perform(get("/api/_search/statuses?query=id:" + status.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(status.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }
}
