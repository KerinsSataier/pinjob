package ru.pinjob.repository;

import ru.pinjob.domain.Rate;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Rate entity.
 */
@SuppressWarnings("unused")
public interface RateRepository extends JpaRepository<Rate,Long> {

    @Query("select rate from Rate rate where rate.user.login = ?#{principal.username}")
    List<Rate> findByUserIsCurrentUser();

}
