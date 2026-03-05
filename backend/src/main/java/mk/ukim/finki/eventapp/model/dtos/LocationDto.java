package mk.ukim.finki.eventapp.model.dtos;

import lombok.Data;

@Data
public class LocationDto {
    Long id;
    String name;
    int capacity;
    double longitude;
    double latitude;
}
