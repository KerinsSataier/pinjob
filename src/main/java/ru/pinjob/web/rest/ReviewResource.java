package ru.pinjob.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.pinjob.domain.Review;

import ru.pinjob.repository.ReviewRepository;
import ru.pinjob.repository.UserRepository;
import ru.pinjob.repository.search.ReviewSearchRepository;
import ru.pinjob.security.SecurityUtils;
import ru.pinjob.web.rest.util.HeaderUtil;
import ru.pinjob.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private ReviewSearchRepository reviewSearchRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * POST  /reviews : Create a new review.
     *
     * @param review the review to create
     * @return the ResponseEntity with status 201 (Created) and with body the new review, or with status 400 (Bad Request) if the review has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> createReview(@RequestBody Review review) throws URISyntaxException {
        log.debug("REST request to save Review : {}", review);
        if (review.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("review", "idexists", "A new review cannot already have an ID")).body(null);
        }

        review.setAuthor(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        Review result = reviewRepository.save(review);
        reviewSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("review", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reviews : Updates an existing review.
     *
     * @param review the review to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated review,
     * or with status 400 (Bad Request) if the review is not valid,
     * or with status 500 (Internal Server Error) if the review couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> updateReview(@Valid @RequestBody Review review) throws URISyntaxException {
        log.debug("REST request to update Review : {}", review);
        if (review.getId() == null) {
            return createReview(review);
        }
        Review result = reviewRepository.save(review);
        reviewSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("review", review.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reviews : get all the reviews.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of reviews in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Review>> getAllReviews(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Reviews");
        Page<Review> page = reviewRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/reviews");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /reviews/:id : get the "id" review.
     *
     * @param id the id of the review to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the review, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        Review review = reviewRepository.findOne(id);
        return Optional.ofNullable(review)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reviews/:id : delete the "id" review.
     *
     * @param id the id of the review to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.debug("REST request to delete Review : {}", id);
        reviewRepository.delete(id);
        reviewSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("review", id.toString())).build();
    }

    /**
     * SEARCH  /_search/reviews?query=:query : search for the review corresponding
     * to the query.
     *
     * @param query the query of the review search
     * @param pageable the pagination information
     * @return the result of the search
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/_search/reviews",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Review>> searchReviews(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Reviews for query {}", query);
        Page<Review> page = reviewSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/reviews");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
