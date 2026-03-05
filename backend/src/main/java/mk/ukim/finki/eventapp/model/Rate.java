package mk.ukim.finki.eventapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "event_ratings")
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private Integer rate;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    @JsonBackReference
    @JsonManagedReference
    private User user;


    @ManyToOne
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private Event event;


    public Rate(Integer rate) {
        this.rate = rate;
    }

    private LocalDateTime ratedAt;

    @PrePersist
    protected void onCreate() {
        ratedAt = LocalDateTime.now();
    }
}