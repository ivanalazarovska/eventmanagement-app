package mk.ukim.finki.eventapp.repository;

import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.Location;
import mk.ukim.finki.eventapp.model.enumerations.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {



    List<Event> findByType(Type type);

    List<Event> findByCreatorId(Long creatorId);

    List<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    List<Event> findByStartTimeBeforeAndEndTimeAfter(LocalDateTime startTime, LocalDateTime endTime);

    List<Event> findByEndTimeBefore(LocalDateTime endTime);

    List<Event> findByStartTimeAfter(LocalDateTime startTime);

    List<Event> findAllByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Event> findByNameContainingIgnoreCase(String keyword);

    List<Event> findByDescriptionContainingIgnoreCase(String keyword);

    List<Event> findByTypeOrderByStartTimeAsc(Type type);

    List<Event> findByTypeOrderByStartTimeDesc(Type type);

    List<Event> findByLocationAndStartTimeBeforeAndEndTimeAfter(Location location, LocalDateTime endTime, LocalDateTime startTime);
}
