package mk.ukim.finki.eventapp.web.controller;

import mk.ukim.finki.eventapp.model.dtos.RegisterDto;
import mk.ukim.finki.eventapp.model.enumerations.Role;
import mk.ukim.finki.eventapp.model.exceptions.InvalidArgumentsException;
import mk.ukim.finki.eventapp.model.exceptions.PasswordsDoNotMatchException;
import mk.ukim.finki.eventapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@Validated
@CrossOrigin(origins="*")
public class RegisterController {
    private final AuthService authService;

    public RegisterController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("")
    public ResponseEntity<String> register(@RequestBody RegisterDto dto) {
        try {
            this.authService.register(dto.getUsername(), dto.getEmail(),
                    dto.getPassword(), dto.getRepeatPassword(), dto.getName(), dto.getSurname(), dto.getRole());
            return ResponseEntity.ok("Registration successful");

        } catch (PasswordsDoNotMatchException | InvalidArgumentsException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}

