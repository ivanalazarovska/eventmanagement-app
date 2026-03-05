package mk.ukim.finki.eventapp.web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mk.ukim.finki.eventapp.config.TokenService;
import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.model.dtos.LogInDto;
import mk.ukim.finki.eventapp.model.exceptions.InvalidCredentialsException;
import mk.ukim.finki.eventapp.model.exceptions.InvalidUserCredentialsException;
import mk.ukim.finki.eventapp.repository.UserRepository;
import mk.ukim.finki.eventapp.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/login")
@Validated
@CrossOrigin(origins="*")
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthService authService;
    private final UserRepository userRepository;

    public LoginController(AuthenticationManager authenticationManager, TokenService tokenService, AuthService authService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("")
    public ResponseEntity<Object> login(@RequestBody LogInDto loginData, HttpServletResponse response) {
        try{
            Optional<User> userOptional = userRepository.findByEmail(loginData.getEmail());
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("User with the provided email does not exist.");
            }

            var authenticationToken = new UsernamePasswordAuthenticationToken(loginData.getEmail(), loginData.getPassword());
            var authentication = authenticationManager.authenticate(authenticationToken);

            var user = (User) authentication.getPrincipal();
            var token = tokenService.generateToken(user);
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            return ResponseEntity.ok().body(Map.of("token", token, "email", user.getEmail(), "name", user.getName(), "role", user.getRole()));
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User with the provided email does not exist.");
        } catch (InvalidUserCredentialsException | InvalidCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred. Please try again later." + e.getMessage());
        }

    }


}