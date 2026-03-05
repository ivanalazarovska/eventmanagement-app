package mk.ukim.finki.eventapp.service;


import jakarta.transaction.Transactional;
import mk.ukim.finki.eventapp.model.Comment;
import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.dtos.CommentDto;
import mk.ukim.finki.eventapp.model.dtos.EventCreateEditRequestDto;
import mk.ukim.finki.eventapp.model.dtos.EventRequestDTO;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.enumerations.ParticipationStatus;
import mk.ukim.finki.eventapp.model.enumerations.Type;

import java.time.LocalDateTime;
import java.util.List;


public interface EventService {

    Event createEvent(EventCreateEditRequestDto eventRequestDTO);
    Event editEvent(Long id, EventCreateEditRequestDto eventRequestDTO);

    List<Event> getEventsByType(Type type);


    List<EventRequestDTO> getAllEvents();

    boolean deleteEvent(Long id);


    List<EventRequestDTO> getCurrentEvents();

    List<EventRequestDTO> getPastEvents();

    List<EventRequestDTO> getUpcomingEvents();

    EventRequestDTO getEventById(Long id);

    void registerForEvent(Long eventId, List<Long> userIds);
    void setParticipationStatus(Long id, ParticipationStatus status);

    List<EventRequestDTO> getEventsForToday(LocalDateTime startOfDay, LocalDateTime endOfDay);

    Event addCommentToEvent(Long id, String comment);

    @Transactional
     Event addRatingToEvent(Long eventId, Integer rate);

    List<CommentDto> getCommentsForEvent(Long eventId);
}
