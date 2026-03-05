package mk.ukim.finki.eventapp.service;


import mk.ukim.finki.eventapp.model.Event;
import mk.ukim.finki.eventapp.model.User;

import java.util.List;

public interface UserService {


    User findByUsername(String username);


    List<User> findAllUsers();

    List<Event> getFavoriteEvents(Long userId);

    User save(User user);


    boolean deleteUser(Long id);

    User findById(Long id);

}