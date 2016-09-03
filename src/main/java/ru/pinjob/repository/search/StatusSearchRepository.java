package ru.pinjob.repository.search;

import ru.pinjob.domain.Status;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Status entity.
 */
public interface StatusSearchRepository extends ElasticsearchRepository<Status, Long> {
}
