package ru.pinjob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.pinjob.domain.Status;

import ru.pinjob.repository.StatusRepository;
import ru.pinjob.repository.search.StatusSearchRepository;
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
 * REST controller for managing Status.
 */
@RestController
@RequestMapping("/api")
public class StatusResource {

    private final Logger log = LoggerFactory.getLogger(StatusResource.class);
        
    @Inject
    private StatusRepository statusRepository;

    @Inject
    private StatusSearchRepository statusSearchRepository;

    /**
     * POST  /statuses : Create a new status.
     *
     * @param status the status to create
     * @return the ResponseEntity with status 201 (Created) and with body the new status, or with status 400 (Bad Request) if the status has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/statuses",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Status> createStatus(@Valid @RequestBody Status status) throws URISyntaxException {
        log.debug("REST request to save Status : {}", status);
        if (status.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("status", "idexists", "A new status cannot already have an ID")).body(null);
        }
        Status result = statusRepository.save(status);
        statusSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/statuses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("status", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /statuses : Updates an existing status.
     *
     * @param status the status to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated status,
     * or with status 400 (Bad Request) if the status is not valid,
     * or with status 500 (Internal Server Error) if the status couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/statuses",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Status> updateStatus(@Valid @RequestBody Status status) throws URISyntaxException {
        log.debug("REST request to update Status : {}", status);
        if (status.getId() == null) {
            return createStatus(status);
        }
        Status result = statusRepository.save(status);
        statusSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("status", status.getId().toString()))
            .body(result);
    }

    /**
     * GET  /statuses : get all the statuses.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of statuses in body
     */
    @RequestMapping(value = "/statuses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Status> getAllStatuses() {
        log.debug("REST request to get all Statuses");
        List<Status> statuses = statusRepository.findAll();
        return statuses;
    }

    /**
     * GET  /statuses/:id : get the "id" status.
     *
     * @param id the id of the status to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the status, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/statuses/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Status> getStatus(@PathVariable Long id) {
        log.debug("REST request to get Status : {}", id);
        Status status = statusRepository.findOne(id);
        return Optional.ofNullable(status)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /statuses/:id : delete the "id" status.
     *
     * @param id the id of the status to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/statuses/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteStatus(@PathVariable Long id) {
        log.debug("REST request to delete Status : {}", id);
        statusRepository.delete(id);
        statusSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("status", id.toString())).build();
    }

    /**
     * SEARCH  /_search/statuses?query=:query : search for the status corresponding
     * to the query.
     *
     * @param query the query of the status search 
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/statuses",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Status> searchStatuses(@RequestParam String query) {
        log.debug("REST request to search Statuses for query {}", query);
        return StreamSupport
            .stream(statusSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
