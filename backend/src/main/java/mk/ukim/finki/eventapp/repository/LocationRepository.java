package mk.ukim.finki.eventapp.repository;

import mk.ukim.finki.eventapp.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Location findByName(String name);
    Optional<Location> findById(Long id);
}
