package mk.ukim.finki.eventapp.model.dtos;

import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.enumerations.Status;
import mk.ukim.finki.eventapp.model.enumerations.Type;

import java.time.LocalDateTime;

public class MyEventDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Type type;
    private String imageUrl;
    private String organizer;
    private String location;
    private Status status;

}
