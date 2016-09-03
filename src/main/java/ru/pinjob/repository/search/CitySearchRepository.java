package ru.pinjob.repository.search;

import ru.pinjob.domain.City;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the City entity.
 */
public interface CitySearchRepository extends ElasticsearchRepository<City, Long> {
}
