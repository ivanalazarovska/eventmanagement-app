package mk.ukim.finki.eventapp.repository;

import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.UserEventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEventParticipationRepository extends JpaRepository<UserEventParticipation, Long> {
    Optional<UserEventParticipation> findByUserAndEvent(User user, Event event);
    List<UserEventParticipation> findAllByEvent(Event event);
}
