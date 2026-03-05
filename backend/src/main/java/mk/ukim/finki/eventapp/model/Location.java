package mk.ukim.finki.eventapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int capacity;
    private Double longitude;
    private Double latitude;

    @OneToMany(mappedBy = "location")
    @ToString.Exclude
    @JsonIgnore
    private List<Event> events;

    public Location(Long id, String name, int capacity, Double longitude, Double latitude) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.longitude = longitude;
        this.latitude = latitude;
        events = new ArrayList<>();
    }
}
