package mk.ukim.finki.eventapp.web.controller;

import mk.ukim.finki.eventapp.model.User;
import mk.ukim.finki.eventapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/change-password")
@CrossOrigin(origins = "*")
public class ChangePasswordController{

    private final AuthService authService;

    public ChangePasswordController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("")
    public ResponseEntity<Object> changePassword(@RequestParam String username,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword,
                                                 @RequestParam String repeatNewPassword) {
        try {
            User user = authService.changePassword(username, oldPassword, newPassword, repeatNewPassword);
            return ResponseEntity.ok(user);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}
