package mk.ukim.finki.eventapp.service.impl;

import jakarta.transaction.Transactional;
import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.exceptions.DuplicateEntityException;
import mk.ukim.finki.eventapp.model.exceptions.EventNotFoundException;
import mk.ukim.finki.eventapp.model.exceptions.UserNotFoundException;
import mk.ukim.finki.eventapp.repository.EventRepository;
import mk.ukim.finki.eventapp.repository.UserRepository;
import mk.ukim.finki.eventapp.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    public UserServiceImpl(UserRepository userRepository, EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;

    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Event> getFavoriteEvents(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return user.get().getFavoriteEvents(); // Враќате список на омилени настани
        } else {
            throw new UserNotFoundException("User not found with id: " + userId); // Ако не постои корисник
        }
    }

    @Override
    public User save(User user) {
        return this.userRepository.save(user);
    }

    public boolean deleteUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    @Transactional
    public User addFavoriteEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));


        if (user.getFavoriteEvents().contains(event)) {
            throw new DuplicateEntityException("Event already in favorites");
        }

        user.getFavoriteEvents().add(event);
        return userRepository.save(user);
    }


    @Transactional
    public User removeFavoriteEvent(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        user.getFavoriteEvents().removeIf(event -> event.getId().equals(eventId));
        return userRepository.save(user);
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}