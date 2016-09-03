package ru.pinjob.repository.search;

import ru.pinjob.domain.Announcement;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Announcement entity.
 */
public interface AnnouncementSearchRepository extends ElasticsearchRepository<Announcement, Long> {
}
