package mk.ukim.finki.eventapp.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.eventapp.model.enumerations.Status;
import mk.ukim.finki.eventapp.model.enumerations.Type;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateEditRequestDto {
    private String name;
    private String description;
    private Long locationId;
    private int locationCapacity;
    private Status status;
    private Type type;
    private MultipartFile image;
    private int price;
    private int capacity;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;


    private String organizer;
}
