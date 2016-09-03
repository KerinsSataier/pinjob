package ru.pinjob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.pinjob.domain.Announcement;

import ru.pinjob.repository.AnnouncementRepository;
import ru.pinjob.repository.search.AnnouncementSearchRepository;
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
 * REST controller for managing Announcement.
 */
@RestController
@RequestMapping("/api")
public class AnnouncementResource {

    private final Logger log = LoggerFactory.getLogger(AnnouncementResource.class);
        
    @Inject
    private AnnouncementRepository announcementRepository;

    @Inject
    private AnnouncementSearchRepository announcementSearchRepository;

    /**
     * POST  /announcements : Create a new announcement.
     *
     * @param announcement the announcement to create
     * @return the ResponseEntity with status 201 (Created) and with body the new announcement, or with status 400 (Bad Request) if the announcement has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/announcements",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Announcement> createAnnouncement(@Valid @RequestBody Announcement announcement) throws URISyntaxException {
        log.debug("REST request to save Announcement : {}", announcement);
        if (announcement.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("announcement", "idexists", "A new announcement cannot already have an ID")).body(null);
        }
        Announcement result = announcementRepository.save(announcement);
        announcementSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/announcements/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("announcement", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /announcements : Updates an existing announcement.
     *
     * @param announcement the announcement to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated announcement,
     * or with status 400 (Bad Request) if the announcement is not valid,
     * or with status 500 (Internal Server Error) if the announcement couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/announcements",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Announcement> updateAnnouncement(@Valid @RequestBody Announcement announcement) throws URISyntaxException {
        log.debug("REST request to update Announcement : {}", announcement);
        if (announcement.getId() == null) {
            return createAnnouncement(announcement);
        }
        Announcement result = announcementRepository.save(announcement);
        announcementSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("announcement", announcement.getId().toString()))
            .body(result);
    }

    /**
     * GET  /announcements : get all the announcements.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of announcements in body
     */
    @RequestMapping(value = "/announcements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Announcement> getAllAnnouncements() {
        log.debug("REST request to get all Announcements");
        List<Announcement> announcements = announcementRepository.findAll();
        return announcements;
    }

    /**
     * GET  /announcements/:id : get the "id" announcement.
     *
     * @param id the id of the announcement to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the announcement, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/announcements/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Announcement> getAnnouncement(@PathVariable Long id) {
        log.debug("REST request to get Announcement : {}", id);
        Announcement announcement = announcementRepository.findOne(id);
        return Optional.ofNullable(announcement)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /announcements/:id : delete the "id" announcement.
     *
     * @param id the id of the announcement to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/announcements/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteAnnouncement(@PathVariable Long id) {
        log.debug("REST request to delete Announcement : {}", id);
        announcementRepository.delete(id);
        announcementSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("announcement", id.toString())).build();
    }

    /**
     * SEARCH  /_search/announcements?query=:query : search for the announcement corresponding
     * to the query.
     *
     * @param query the query of the announcement search 
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/announcements",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Announcement> searchAnnouncements(@RequestParam String query) {
        log.debug("REST request to search Announcements for query {}", query);
        return StreamSupport
            .stream(announcementSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }


}
