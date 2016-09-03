package ru.pinjob.repository.search;

import ru.pinjob.domain.Review;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Review entity.
 */
public interface ReviewSearchRepository extends ElasticsearchRepository<Review, Long> {
}
