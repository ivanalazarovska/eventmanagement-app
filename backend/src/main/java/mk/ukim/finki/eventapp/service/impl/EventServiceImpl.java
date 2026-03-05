package mk.ukim.finki.eventapp.service.impl;
import jakarta.transaction.Transactional;
import mk.ukim.finki.eventapp.model.*;
import mk.ukim.finki.eventapp.model.dtos.CommentDto;
import mk.ukim.finki.eventapp.model.dtos.EventCreateEditRequestDto;
import mk.ukim.finki.eventapp.model.dtos.EventRequestDTO;
import mk.ukim.finki.eventapp.model.enumerations.ParticipationStatus;
import mk.ukim.finki.eventapp.model.enumerations.Status;
import mk.ukim.finki.eventapp.model.enumerations.Type;
import mk.ukim.finki.eventapp.model.exceptions.EventNotFoundException;
import mk.ukim.finki.eventapp.model.exceptions.InvalidRatingException;
import mk.ukim.finki.eventapp.model.exceptions.InvalidUsernameException;
import mk.ukim.finki.eventapp.repository.*;
import mk.ukim.finki.eventapp.service.AuthService;
import mk.ukim.finki.eventapp.service.EventService;
import mk.ukim.finki.eventapp.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {


    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final UserService userService;

    private final CommentRepository commentRepository;

    private final RateRepository rateRepository;

    private final UserEventParticipationRepository userEventParticipationRepository;
    private final AuthService authService;
    private final LocationRepository locationRepository;

    private static final String UPLOAD_DIR_PATH = "uploads/";
    private static final String UPLOAD_URL = "http://localhost:9090/uploads/";

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, UserService userService, CommentRepository commentRepository, RateRepository rateRepository, UserEventParticipationRepository userEventParticipationRepository, AuthService authService, LocationRepository locationRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.rateRepository = rateRepository;
        this.userEventParticipationRepository = userEventParticipationRepository;
        this.authService = authService;
        this.locationRepository = locationRepository;
    }

    @Override
    public Event createEvent(EventCreateEditRequestDto dto) {
        Type type;
        try {
            type = Type.valueOf(dto.getType().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid event type: " + dto.getType());
        }

        String originalFilename = dto.getImage().getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + fileExtension;

        File uploadPath = new File(UPLOAD_DIR_PATH);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        Path filePath = Paths.get(UPLOAD_DIR_PATH, uniqueFilename);
        try {
            Files.write(filePath, dto.getImage().getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid image file: " + e.getMessage());
        }

        // This is what you'll store in the DB
        String imageUrl = UPLOAD_URL + uniqueFilename;

        Location location = locationRepository.findById(dto.getLocationId()).orElseThrow(
                () -> new IllegalArgumentException("Location not found with ID: " + dto.getLocationId())
        );
        User user = authService.getCurrentlyLoggedInUser();

        Event event = Event.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .location(location)
                .status(dto.getStatus())
                .type(type)
                .createdAt(LocalDateTime.now())
                .price(dto.getPrice())
                .capacity(dto.getCapacity())
                .imageUrl(imageUrl)
                .creator(userService.findById(user.getId()))
                .organizer(dto.getOrganizer())
                .build();

        return eventRepository.save(event);
    }

    public Event editEvent(Long id, EventCreateEditRequestDto dto) {
        // 1. Fetch existing event
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + id));

        // 2. Validate and parse enums
        Type type;
        try {
            type = Type.valueOf(dto.getType().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid event type: " + dto.getType());
        }

        Status status;
        try {
            status = Status.valueOf(dto.getStatus().toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid event status: " + dto.getStatus());
        }

        // 3. Handle image update (only if a new file is uploaded and is different)
        String imageUrl = existingEvent.getImageUrl();

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String uploadedFilename = dto.getImage().getOriginalFilename();

            if (uploadedFilename != null && !imageUrl.endsWith(uploadedFilename)) {
                String fileExtension = uploadedFilename.substring(uploadedFilename.lastIndexOf("."));
                String uniqueFilename = UUID.randomUUID() + fileExtension;

                File uploadPath = new File(UPLOAD_DIR_PATH);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                Path filePath = Paths.get(UPLOAD_DIR_PATH, uniqueFilename);
                try {
                    Files.write(filePath, dto.getImage().getBytes());
                } catch (IOException e) {
                    throw new IllegalArgumentException("Failed to save new image: " + e.getMessage());
                }

                imageUrl = UPLOAD_URL + uniqueFilename;
            }
        }

        Location location = locationRepository.findById(dto.getLocationId()).orElseThrow(
                () -> new IllegalArgumentException("Location not found with ID: " + dto.getLocationId())
        );
        // 4. Update fields
        existingEvent.setName(dto.getName());
        existingEvent.setDescription(dto.getDescription());
        existingEvent.setStartTime(dto.getStartTime());
        existingEvent.setEndTime(dto.getEndTime());
        existingEvent.setLocation(location);
        existingEvent.setType(type);
        existingEvent.setStatus(status);
        existingEvent.setPrice(dto.getPrice());
        existingEvent.setCapacity(dto.getCapacity());
        existingEvent.setOrganizer(dto.getOrganizer());
        existingEvent.setImageUrl(imageUrl);

        return eventRepository.save(existingEvent);
    }




    @Override
    public List<Event> getEventsByType(Type type) {
        return eventRepository.findByType(type);
    }

    @Override
    public List<EventRequestDTO> getAllEvents() {

        return eventRepository.findAll().stream().map(event -> {
            EventRequestDTO dto = new EventRequestDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setDescription(event.getDescription());
            dto.setStartTime(event.getStartTime());
            dto.setEndTime(event.getEndTime());
            dto.setLocationId(event.getLocation().getId());
            dto.setLocation(event.getLocation().getName());
            dto.setLocationCapacity(event.getLocation().getCapacity());
            dto.setLongitude(event.getLocation().getLongitude());
            dto.setLatitude(event.getLocation().getLatitude());
            dto.setStatus(event.getStatus());
            dto.setType(event.getType());
            dto.setImageUrl(event.getImageUrl());
            dto.setPrice(event.getPrice());
            dto.setCapacity(event.getCapacity());
            dto.setGoing((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.GOING).count());
            dto.setInterested((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.INTERESTED).count());
            dto.setDeclined((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.DECLINED).count());
            dto.setCreatorId(event.getCreator().getId());
            dto.setCreator(event.getCreator());
            dto.setOrganizer(event.getOrganizer());

            return dto;
        }).toList();
    }

    @Override
    public boolean deleteEvent(Long id) {
        if (eventRepository.existsById(id)) {
            eventRepository.deleteById(id);
            return true;  // настанот е успешно избришан
        } else {
            return false;  // настанот со даденото ID не постои
        }
    }

    @Override
    public List<EventRequestDTO> getCurrentEvents() {
        LocalDateTime now = LocalDateTime.now();

        return eventRepository.findByStartTimeBeforeAndEndTimeAfter(now, now).stream().map(event -> {
            EventRequestDTO dto = new EventRequestDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setDescription(event.getDescription());
            dto.setStartTime(event.getStartTime());
            dto.setEndTime(event.getEndTime());
            dto.setLocationId(event.getLocation().getId());
            dto.setLocation(event.getLocation().getName());
            dto.setLocationCapacity(event.getLocation().getCapacity());
            dto.setLongitude(event.getLocation().getLongitude());
            dto.setLatitude(event.getLocation().getLatitude());
            dto.setStatus(event.getStatus());
            dto.setType(event.getType());
            dto.setImageUrl(event.getImageUrl());
            dto.setPrice(event.getPrice());
            dto.setCapacity(event.getCapacity());
            dto.setGoing((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.GOING).count());
            dto.setInterested((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.INTERESTED).count());
            dto.setDeclined((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.DECLINED).count());
            dto.setCreatorId(event.getCreator().getId());
            dto.setCreator(event.getCreator());
            dto.setOrganizer(event.getOrganizer());

            return dto;
        }).toList();
    }

    @Override
    public List<EventRequestDTO> getPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByEndTimeBefore(now).stream().map(event -> {
            EventRequestDTO dto = new EventRequestDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setDescription(event.getDescription());
            dto.setStartTime(event.getStartTime());
            dto.setEndTime(event.getEndTime());
            dto.setLocationId(event.getLocation().getId());
            dto.setLocation(event.getLocation().getName());
            dto.setLocationCapacity(event.getLocation().getCapacity());
            dto.setLongitude(event.getLocation().getLongitude());
            dto.setLatitude(event.getLocation().getLatitude());
            dto.setStatus(event.getStatus());
            dto.setType(event.getType());
            dto.setImageUrl(event.getImageUrl());
            dto.setPrice(event.getPrice());
            dto.setCapacity(event.getCapacity());
            dto.setGoing((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.GOING).count());
            dto.setInterested((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.INTERESTED).count());
            dto.setDeclined((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.DECLINED).count());
            dto.setCreatorId(event.getCreator().getId());
            dto.setCreator(event.getCreator());
            dto.setOrganizer(event.getOrganizer());

            return dto;
        }).toList();
    }

    @Override
    public List<EventRequestDTO> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findByStartTimeAfter(now).stream().map(event -> {
            EventRequestDTO dto = new EventRequestDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setDescription(event.getDescription());
            dto.setStartTime(event.getStartTime());
            dto.setEndTime(event.getEndTime());
            dto.setLocationId(event.getLocation().getId());
            dto.setLocation(event.getLocation().getName());
            dto.setLocationCapacity(event.getLocation().getCapacity());
            dto.setLongitude(event.getLocation().getLongitude());
            dto.setLatitude(event.getLocation().getLatitude());
            dto.setStatus(event.getStatus());
            dto.setType(event.getType());
            dto.setImageUrl(event.getImageUrl());
            dto.setPrice(event.getPrice());
            dto.setCapacity(event.getCapacity());
            dto.setGoing((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.GOING).count());
            dto.setInterested((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.INTERESTED).count());
            dto.setDeclined((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.DECLINED).count());
            dto.setCreatorId(event.getCreator().getId());
            dto.setCreator(event.getCreator());
            dto.setOrganizer(event.getOrganizer());

            return dto;
        }).toList();
    }

    @Override
    public EventRequestDTO getEventById(Long id) {
        User loggedInUser = authService.getCurrentlyLoggedInUser();
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        ParticipationStatus userParticipationStatus = userEventParticipationRepository
                .findByUserAndEvent(loggedInUser, event).map(UserEventParticipation::getStatus).orElse(null);

        EventRequestDTO dto = new EventRequestDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDescription(event.getDescription());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setLocationId(event.getLocation().getId());
        dto.setLocation(event.getLocation().getName());
        dto.setLocationCapacity(event.getLocation().getCapacity());
        dto.setLongitude(event.getLocation().getLongitude());
        dto.setLatitude(event.getLocation().getLatitude());
        dto.setStatus(event.getStatus());
        dto.setType(event.getType());
        dto.setImageUrl(event.getImageUrl());
        dto.setPrice(event.getPrice());
        dto.setCapacity(event.getCapacity());
        dto.setGoing((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.GOING).count());
        dto.setInterested((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.INTERESTED).count());
        dto.setDeclined((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.DECLINED).count());
        dto.setCreatorId(event.getCreator().getId());
        dto.setCreator(event.getCreator());
        dto.setOrganizer(event.getOrganizer());
        dto.setUserParticipationStatus(userParticipationStatus);
        dto.setNumParticipants((int) userEventParticipationRepository.findAllByEvent(event).stream().filter(ue -> ue.getStatus().equals(ParticipationStatus.GOING)).count());

        double averageRating = 0.0;
        if (!event.getRates().isEmpty()) {
            int sumRates = event.getRates().stream().mapToInt(Rate::getRate).sum();
            averageRating = (double) sumRates / event.getRates().size();
        }
        dto.setAverageRating(averageRating);

        List<CommentDto> commentDTOs = event.getComments().stream()
                .map(c -> new CommentDto(c.getId(), c.getComment(), c.getUser().getUsername()))
                .collect(Collectors.toList());

        dto.setComments(commentDTOs);

        return dto;
    }


    @Override
    public void registerForEvent(Long eventId, List<Long> userIds) {
        User loggedInUser = authService.getCurrentlyLoggedInUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));


        if (!userIds.contains(loggedInUser.getId())) {
            userIds.add(loggedInUser.getId());
        }
        List<User> usersToRegister = userRepository.findAllById(userIds);

        for (User user : usersToRegister) {
            Optional<UserEventParticipation> existingParticipation =
                    userEventParticipationRepository.findByUserAndEvent(user, event);

            if(existingParticipation.isEmpty()) {
                UserEventParticipation participation = new UserEventParticipation(user, event, ParticipationStatus.GOING);
                userEventParticipationRepository.save(participation);
            }else if(existingParticipation.get().getStatus() != ParticipationStatus.GOING){
                existingParticipation.get().setStatus(ParticipationStatus.GOING);
                userEventParticipationRepository.save(existingParticipation.get());
            }
        }
    }

    @Override
    public void setParticipationStatus(Long id, ParticipationStatus status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id));
        User user = authService.getCurrentlyLoggedInUser();

        Optional<UserEventParticipation> userParticipation = userEventParticipationRepository.findByUserAndEvent(user, event);

        if(userParticipation.isPresent()){
            userParticipation.get().setStatus(status);
            userEventParticipationRepository.save(userParticipation.get());
        }else{
            UserEventParticipation userEventParticipation = new UserEventParticipation(user, event, status);
            userEventParticipationRepository.save(userEventParticipation);
        }
    }

    @Override

    public List<EventRequestDTO> getEventsForToday(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return eventRepository.findAllByStartTimeBetween(startOfDay, endOfDay).stream().map(event -> {
            EventRequestDTO dto = new EventRequestDTO();
            dto.setId(event.getId());
            dto.setName(event.getName());
            dto.setDescription(event.getDescription());
            dto.setStartTime(event.getStartTime());
            dto.setEndTime(event.getEndTime());
            dto.setLocationId(event.getLocation().getId());
            dto.setLocation(event.getLocation().getName());
            dto.setLocationCapacity(event.getLocation().getCapacity());
            dto.setLongitude(event.getLocation().getLongitude());
            dto.setLatitude(event.getLocation().getLatitude());
            dto.setStatus(event.getStatus());
            dto.setType(event.getType());
            dto.setImageUrl(event.getImageUrl());
            dto.setPrice(event.getPrice());
            dto.setCapacity(event.getCapacity());
            dto.setGoing((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.GOING).count());
            dto.setInterested((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.INTERESTED).count());
            dto.setDeclined((int) event.getParticipants().stream().filter(p -> p.getStatus() == ParticipationStatus.DECLINED).count());
            dto.setCreatorId(event.getCreator().getId());
            dto.setCreator(event.getCreator());
            dto.setOrganizer(event.getOrganizer());

            return dto;
        }).toList();
    }


    @Override
    @Transactional
    public Event addCommentToEvent(Long id, String commentText) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Event not found!"));
        User user = authService.getCurrentlyLoggedInUser();

        Comment newComment = new Comment(commentText);
        newComment.setEvent(event);
        newComment.setUser(user);
        this.commentRepository.save(newComment);

//        user.getComments().add(newComment);
//        this.userRepository.save(user);

//        event.getComments().add(newComment);
//        this.eventRepository.save(event);

        return event;

    }


// @Transactional
// public Event addRatingToEvent(Long eventId, Integer rate) {
//
//     Event event = this.eventRepository.findById(eventId)
//             .orElseThrow(() -> new EventNotFoundException("Event not found!"));
//
//     if (rate < 1 || rate > 5) {
//         throw new InvalidRatingException("Rating must be between 1 and 5");
//     }
//
//
//     Rate newRate = new Rate(rate);
//     newRate.setEvent(event);
//     this.rateRepository.save(newRate);
//
//
//     event.getRates().add(newRate);
//
//
//     int sumRates = event.getRates().stream().mapToInt(Rate::getRate).sum();
//     event.setRating((double)sumRates / event.getRates().size());
//
//
//     this.eventRepository.save(event);
//
//     return event;
// }

    @Transactional
    @Override
    public Event addRatingToEvent(Long eventId, Integer rate) {
        Event event = this.eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found!"));

        if (rate < 1 || rate > 5) {
            throw new InvalidRatingException("Rating must be between 1 and 5");
        }

        User user = authService.getCurrentlyLoggedInUser();

        Rate newRate = new Rate(rate);
        newRate.setEvent(event);
        newRate.setUser(user);

        this.rateRepository.save(newRate);

        int sumRates = event.getRates().stream().mapToInt(Rate::getRate).sum();
        event.setRating((double) sumRates / event.getRates().size());

        this.eventRepository.save(event);

        return event;
    }

    @Override
    public List<CommentDto> getCommentsForEvent(Long wineryId) {
        Event event = this.eventRepository.findById(wineryId).orElseThrow(() -> new EventNotFoundException("Event not found"));
        List<CommentDto> comments = this.commentRepository.findAllByEvent(event).stream().map(comment -> {
            CommentDto commentDto = new CommentDto();
            commentDto.setText(comment.getComment());
            commentDto.setUsername(comment.getUser().getUsername());
            commentDto.setId(comment.getId());
            return commentDto;
        }).collect(Collectors.toList());
        return comments;
    }


}
