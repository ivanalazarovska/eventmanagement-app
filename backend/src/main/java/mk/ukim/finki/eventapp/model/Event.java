package mk.ukim.finki.eventapp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import mk.ukim.finki.eventapp.model.enumerations.Status;
import mk.ukim.finki.eventapp.model.enumerations.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "events")
@ToString(exclude = {"participants", "comments", "rates", "location"})
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;
    private String name;
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String imageUrl;
    private int price;
    private int capacity;
    private String organizer;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    @JsonBackReference // Prevent circular reference on creator side
    private User creator;

    // Ignore this field to prevent infinite recursion
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserEventParticipation> participants = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Rate> rates = new ArrayList<>();
}

