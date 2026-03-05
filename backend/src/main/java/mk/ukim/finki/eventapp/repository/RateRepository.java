package mk.ukim.finki.eventapp.repository;

import mk.ukim.finki.eventapp.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    List<Rate> findByEventId(Long eventId);
    List<Rate> findByUserId(Long userId);


}