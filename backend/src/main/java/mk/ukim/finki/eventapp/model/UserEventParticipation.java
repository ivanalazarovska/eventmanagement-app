package mk.ukim.finki.eventapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.eventapp.model.enumerations.ParticipationStatus;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_event_participations")
public class UserEventParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    private ParticipationStatus status;

    private LocalDateTime joinedAt;

    public UserEventParticipation(User user, Event event, ParticipationStatus status) {
        this.user = user;
        this.event = event;
        this.status = status;
        this.joinedAt = LocalDateTime.now();
    }
}
