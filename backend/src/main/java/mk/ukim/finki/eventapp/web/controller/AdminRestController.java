package mk.ukim.finki.eventapp.web.controller;

import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.enumerations.Role;
import mk.ukim.finki.eventapp.service.EventService;
import mk.ukim.finki.eventapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public AdminRestController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }


    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }


}
