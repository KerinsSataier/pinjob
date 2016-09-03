package ru.pinjob.repository.search;

import ru.pinjob.domain.Rate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Rate entity.
 */
public interface RateSearchRepository extends ElasticsearchRepository<Rate, Long> {
}
