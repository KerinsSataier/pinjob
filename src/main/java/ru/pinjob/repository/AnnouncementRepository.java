package ru.pinjob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.pinjob.domain.Announcement;

import org.springframework.data.jpa.repository.*;
import ru.pinjob.domain.projections.SimplifiedAnnouncement;

import java.util.List;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
@SuppressWarnings("unused")
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {

    @Query("select announcement from Announcement announcement where announcement.user.login = ?#{principal.username}")
    List<Announcement> findByUserIsCurrentUser();

    @Query("SELECT a.id AS id, a.title AS title, a.price AS price, a.place AS place FROM Announcement a")
    Page<SimplifiedAnnouncement> getAllSimplifiedAnnouncements(Pageable pageable);

}
