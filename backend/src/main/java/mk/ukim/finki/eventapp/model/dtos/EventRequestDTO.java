package mk.ukim.finki.eventapp.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.enumerations.ParticipationStatus;
import mk.ukim.finki.eventapp.model.enumerations.Status;
import mk.ukim.finki.eventapp.model.enumerations.Type;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Type type;
    private String imageUrl;
    private int price;
    private int capacity;
    private Long creatorId;
    private User creator;
    private String organizer;

    private Long locationId;
    private String location;
    private double longitude;
    private double latitude;
    private int locationCapacity;

    private Status status;

    private int going;
    private int interested;
    private int declined;
    private ParticipationStatus userParticipationStatus;
    private int numParticipants;

    private double averageRating;

    private List<CommentDto> comments;
}

