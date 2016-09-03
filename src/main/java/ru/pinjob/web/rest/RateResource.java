package ru.pinjob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.pinjob.domain.Rate;

import ru.pinjob.repository.RateRepository;
import ru.pinjob.repository.search.RateSearchRepository;
import ru.pinjob.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Rate.
 */
@RestController
@RequestMapping("/api")
public class RateResource {

    private final Logger log = LoggerFactory.getLogger(RateResource.class);
        
    @Inject
    private RateRepository rateRepository;

    @Inject
    private RateSearchRepository rateSearchRepository;

    /**
     * POST  /rates : Create a new rate.
     *
     * @param rate the rate to create
     * @return the ResponseEntity with status 201 (Created) and with body the new rate, or with status 400 (Bad Request) if the rate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/rates",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Rate> createRate(@Valid @RequestBody Rate rate) throws URISyntaxException {
        log.debug("REST request to save Rate : {}", rate);
        if (rate.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("rate", "idexists", "A new rate cannot already have an ID")).body(null);
        }
        Rate result = rateRepository.save(rate);
        rateSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/rates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("rate", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /rates : Updates an existing rate.
     *
     * @param rate the rate to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated rate,
     * or with status 400 (Bad Request) if the rate is not valid,
     * or with status 500 (Internal Server Error) if the rate couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/rates",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Rate> updateRate(@Valid @RequestBody Rate rate) throws URISyntaxException {
        log.debug("REST request to update Rate : {}", rate);
        if (rate.getId() == null) {
            return createRate(rate);
        }
        Rate result = rateRepository.save(rate);
        rateSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("rate", rate.getId().toString()))
            .body(result);
    }

    /**
     * GET  /rates : get all the rates.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of rates in body
     */
    @RequestMapping(value = "/rates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Rate> getAllRates() {
        log.debug("REST request to get all Rates");
        List<Rate> rates = rateRepository.findAll();
        return rates;
    }

    /**
     * GET  /rates/:id : get the "id" rate.
     *
     * @param id the id of the rate to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the rate, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/rates/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Rate> getRate(@PathVariable Long id) {
        log.debug("REST request to get Rate : {}", id);
        Rate rate = rateRepository.findOne(id);
        return Optional.ofNullable(rate)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rates/:id : delete the "id" rate.
     *
     * @param id the id of the rate to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/rates/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteRate(@PathVariable Long id) {
        log.debug("REST request to delete Rate : {}", id);
        rateRepository.delete(id);
        rateSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("rate", id.toString())).build();
    }

    /**
     * SEARCH  /_search/rates?query=:query : search for the rate corresponding
     * to the query.
     *
     * @param query the query of the rate search 
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/rates",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Rate> searchRates(@RequestParam String query) {
        log.debug("REST request to search Rates for query {}", query);
        return StreamSupport
            .stream(rateSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
