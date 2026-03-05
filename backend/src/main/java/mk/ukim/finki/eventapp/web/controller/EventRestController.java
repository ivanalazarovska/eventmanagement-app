package mk.ukim.finki.eventapp.web.controller;

import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.dtos.EventCreateEditRequestDto;
import mk.ukim.finki.eventapp.model.dtos.EventRequestDTO;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.enumerations.ParticipationStatus;
import mk.ukim.finki.eventapp.model.exceptions.EventNotFoundException;
import mk.ukim.finki.eventapp.model.exceptions.InvalidUsernameException;
import mk.ukim.finki.eventapp.model.exceptions.UserNotFoundException;
import mk.ukim.finki.eventapp.service.EventService;
import mk.ukim.finki.eventapp.model.enumerations.Type;
import mk.ukim.finki.eventapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping({"/api/events","/"})
@Validated
public class EventRestController {

    private final EventService eventService;

    private final UserService userService;

    @Autowired
    public EventRestController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEvent(@ModelAttribute EventCreateEditRequestDto eventRequestDTO) {
      try {
          Event createdEvent = eventService.createEvent(eventRequestDTO);
          return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
      } catch (IllegalArgumentException e) {

          return ResponseEntity.badRequest().body(e.getMessage());
      } catch (Exception e) {

          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + e.getMessage());
      }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editEvent(@PathVariable Long id, @ModelAttribute EventCreateEditRequestDto eventRequestDTO) {
        try {
            Event createdEvent = eventService.editEvent(id, eventRequestDTO);
            return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error: " + e.getMessage());
        }
    }


    @GetMapping("/type/{type}")
    public List<Event> getEventsByType(@PathVariable Type type) {
        return eventService.getEventsByType(type);
    }


    @GetMapping
    public List<EventRequestDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @DeleteMapping("/{id}")
    public boolean deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id);
    }

    @GetMapping("/upcoming")
    public List<EventRequestDTO> getUpcomingEvents() {
        return eventService.getUpcomingEvents();
    }

    @GetMapping("/past")
    public List<EventRequestDTO> getPastEvents() {
        return eventService.getPastEvents();
    }

    @GetMapping("/current")
    public List<EventRequestDTO> getCurrentEvents() {
        return eventService.getCurrentEvents();
    }
    @GetMapping("/{id}")
    public EventRequestDTO getEventDetails(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/today")
    public List<EventRequestDTO> getTodayEvents() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);


        return eventService.getEventsForToday(startOfDay, endOfDay);
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<String> registerForEvent(@PathVariable Long eventId, @RequestBody List<Long> userIds) {
        try {
            eventService.registerForEvent(eventId, userIds);
            return ResponseEntity.ok("User successfully registered for the event");
        } catch (EventNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{eventId}/likeOrDecline")
    public ResponseEntity<String> setParticipationStatus(
            @PathVariable Long eventId,
            @RequestParam ParticipationStatus status) {
        try {
            eventService.setParticipationStatus(eventId, status);
            return ResponseEntity.ok("Participation status updated successfully");
        } catch (EventNotFoundException | UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<?> getFavoriteEvents(@PathVariable Long userId) {
        try {
            List<Event> favorites = userService.getFavoriteEvents(userId);
            return ResponseEntity.ok(favorites);
        } catch (UserNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }


    @GetMapping("/{id}/comments")
    public ResponseEntity<Object> getCommentsForEvent(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(eventService.getCommentsForEvent(id), HttpStatus.OK);
        } catch (EventNotFoundException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
    @PostMapping("/{eventId}/comments")
    public ResponseEntity<String> commentEvent(@PathVariable Long eventId, @RequestBody String comment) {
        try {
            Event event = eventService.addCommentToEvent(eventId, comment);
            return ResponseEntity.ok("Comment added successfully");
        } catch (EventNotFoundException | InvalidUsernameException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("{id}/add-rating")
    public ResponseEntity<String> rateEvent(@PathVariable Long id, @RequestParam Integer rate){
        try{
            Event event = eventService.addRatingToEvent(id, rate);
            return ResponseEntity.ok("Rate added successfully" + rate);
        } catch(EventNotFoundException ex){
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}

