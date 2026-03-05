package mk.ukim.finki.eventapp.service.impl;


import jakarta.servlet.http.Cookie;
import jakarta.transaction.Transactional;
import mk.ukim.finki.eventapp.config.TokenService;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.dtos.LogInDto;
import mk.ukim.finki.eventapp.model.enumerations.Role;
import mk.ukim.finki.eventapp.model.exceptions.InvalidCredentialsException;
import mk.ukim.finki.eventapp.model.exceptions.InvalidUsernameException;
import mk.ukim.finki.eventapp.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.eventapp.repository.UserRepository;
import mk.ukim.finki.eventapp.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }
    private boolean credentialsInvalid(String username, String password) {
        return username == null || password == null || username.isEmpty() || password.isEmpty();
    }

//    @Override
//    public LogInDto login(String email, String password) {
//        if (credentialsInvalid(email, password)) {
//            throw new InvalidCredentialsException("Invalid credentials");
//        }
//        Optional<User> userOptional =  userRepository.findByEmail(email);
//        if (userOptional.isEmpty()) {
//            throw new IllegalArgumentException("User with this email does not exist");
//        }
//
//        var authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
//        System.out.println("authenticationManager class: " + authenticationManager.getClass().getName());
//        var authentication = authenticationManager.authenticate(authenticationToken);
//        User user = (User) authentication.getPrincipal();
//
//        var token = tokenService.generateToken(user);
//        Cookie jwtCookie = new Cookie("jwt", token);
//        jwtCookie.setHttpOnly(true);
//        jwtCookie.setPath("/");
//
//        return new LogInDto(token, user, jwtCookie);
//    }

    @Override
    public User register(String username, String email, String password, String repeatPassword, String name, String surname, Role role) {
        if (credentialsInvalid(email, password)) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        if(this.userRepository.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("Email already exists");
        }


        String encodedPassword = this.passwordEncoder.encode(password);

        User user = new User(email, username, name, surname, encodedPassword, role);
        return userRepository.save(user);
    }

    @Override
    public List<Cookie> logout(Cookie[] cookies) {
        List<Cookie> returnCookies = new ArrayList<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    tokenService.invalidateToken(token);

                    Cookie jwtCookie = new Cookie("jwt", null);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");
                    jwtCookie.setMaxAge(0);
                    returnCookies.add(jwtCookie);
                }
            }
        }
        return returnCookies;
    }

    @Override
    public List<User> findAll() {
        return this.userRepository.findAll();
    }

    public void createAdminIfNotExists() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setName("Admin");
            admin.setSurname("Adminov");
            admin.setEmail("admin@example.com");
            userRepository.save(admin);
        }
    }

    @Override
    public User changePassword(String username, String oldPassword, String newPassword, String repeatNewPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidUsernameException(username));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        if (!newPassword.equals(repeatNewPassword)) {
            throw new PasswordsDoNotMatchException();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return userRepository.save(user);
    }

    @Override
    public User getCurrentlyLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser;

        if (authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else {
            String currentUserEmail = authentication.getName();
            currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        return currentUser;
    }
}


