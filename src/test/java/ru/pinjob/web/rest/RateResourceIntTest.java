package ru.pinjob.web.rest;

import ru.pinjob.PinjobApp;
import ru.pinjob.domain.Rate;
import ru.pinjob.repository.RateRepository;
import ru.pinjob.repository.search.RateSearchRepository;

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
 * Test class for the RateResource REST controller.
 *
 * @see RateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PinjobApp.class)
public class RateResourceIntTest {

    private static final Integer DEFAULT_VALUE = 1;
    private static final Integer UPDATED_VALUE = 2;

    @Inject
    private RateRepository rateRepository;

    @Inject
    private RateSearchRepository rateSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restRateMockMvc;

    private Rate rate;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RateResource rateResource = new RateResource();
        ReflectionTestUtils.setField(rateResource, "rateSearchRepository", rateSearchRepository);
        ReflectionTestUtils.setField(rateResource, "rateRepository", rateRepository);
        this.restRateMockMvc = MockMvcBuilders.standaloneSetup(rateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Rate createEntity(EntityManager em) {
        Rate rate = new Rate();
        rate = new Rate()
                .value(DEFAULT_VALUE);
        return rate;
    }

    @Before
    public void initTest() {
        rateSearchRepository.deleteAll();
        rate = createEntity(em);
    }

    @Test
    @Transactional
    public void createRate() throws Exception {
        int databaseSizeBeforeCreate = rateRepository.findAll().size();

        // Create the Rate

        restRateMockMvc.perform(post("/api/rates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rate)))
                .andExpect(status().isCreated());

        // Validate the Rate in the database
        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeCreate + 1);
        Rate testRate = rates.get(rates.size() - 1);
        assertThat(testRate.getValue()).isEqualTo(DEFAULT_VALUE);

        // Validate the Rate in ElasticSearch
        Rate rateEs = rateSearchRepository.findOne(testRate.getId());
        assertThat(rateEs).isEqualToComparingFieldByField(testRate);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = rateRepository.findAll().size();
        // set the field null
        rate.setValue(null);

        // Create the Rate, which fails.

        restRateMockMvc.perform(post("/api/rates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(rate)))
                .andExpect(status().isBadRequest());

        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRates() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        // Get all the rates
        restRateMockMvc.perform(get("/api/rates?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(rate.getId().intValue())))
                .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    public void getRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);

        // Get the rate
        restRateMockMvc.perform(get("/api/rates/{id}", rate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(rate.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getNonExistingRate() throws Exception {
        // Get the rate
        restRateMockMvc.perform(get("/api/rates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        rateSearchRepository.save(rate);
        int databaseSizeBeforeUpdate = rateRepository.findAll().size();

        // Update the rate
        Rate updatedRate = rateRepository.findOne(rate.getId());
        updatedRate
                .value(UPDATED_VALUE);

        restRateMockMvc.perform(put("/api/rates")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedRate)))
                .andExpect(status().isOk());

        // Validate the Rate in the database
        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeUpdate);
        Rate testRate = rates.get(rates.size() - 1);
        assertThat(testRate.getValue()).isEqualTo(UPDATED_VALUE);

        // Validate the Rate in ElasticSearch
        Rate rateEs = rateSearchRepository.findOne(testRate.getId());
        assertThat(rateEs).isEqualToComparingFieldByField(testRate);
    }

    @Test
    @Transactional
    public void deleteRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        rateSearchRepository.save(rate);
        int databaseSizeBeforeDelete = rateRepository.findAll().size();

        // Get the rate
        restRateMockMvc.perform(delete("/api/rates/{id}", rate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean rateExistsInEs = rateSearchRepository.exists(rate.getId());
        assertThat(rateExistsInEs).isFalse();

        // Validate the database is empty
        List<Rate> rates = rateRepository.findAll();
        assertThat(rates).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRate() throws Exception {
        // Initialize the database
        rateRepository.saveAndFlush(rate);
        rateSearchRepository.save(rate);

        // Search the rate
        restRateMockMvc.perform(get("/api/_search/rates?query=id:" + rate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(rate.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
