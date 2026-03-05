package mk.ukim.finki.eventapp.web.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mk.ukim.finki.eventapp.config.TokenService;
import mk.ukim.finki.eventapp.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/logout")
@Validated
@CrossOrigin(origins = "*")
public class LogoutController {

    private final AuthService authService;
    private final TokenService tokenService;

    public LogoutController(AuthService authService, TokenService tokenService) {
        this.authService = authService;
        this.tokenService = tokenService;
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    tokenService.invalidateToken(token);

                    Cookie jwtCookie = new Cookie("jwt", null);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");
                    jwtCookie.setMaxAge(0);
                    response.addCookie(jwtCookie);
                }
            }
        }

        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }


    @GetMapping("/profile")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Authenticated!");
    }
}
