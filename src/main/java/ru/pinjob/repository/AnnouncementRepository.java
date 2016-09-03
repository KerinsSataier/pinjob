package ru.pinjob.repository;

import ru.pinjob.domain.Announcement;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Announcement entity.
 */
@SuppressWarnings("unused")
public interface AnnouncementRepository extends JpaRepository<Announcement,Long> {

    @Query("select announcement from Announcement announcement where announcement.user.login = ?#{principal.username}")
    List<Announcement> findByUserIsCurrentUser();

}
