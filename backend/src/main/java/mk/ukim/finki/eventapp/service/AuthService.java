package mk.ukim.finki.eventapp.service;

import jakarta.servlet.http.Cookie;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.dtos.LogInDto;
import mk.ukim.finki.eventapp.model.enumerations.Role;

import java.util.List;

public interface AuthService {
//    LogInDto login(String username, String password);
    User register(String username, String email, String password, String repeatPassword,
                 String name, String surname, Role role);
    List<Cookie> logout(Cookie[] cookies);

    List<User> findAll();

    void createAdminIfNotExists();

    User changePassword(String username, String oldPassword, String newPassword, String repeatNewPassword);
    User getCurrentlyLoggedInUser();

}
