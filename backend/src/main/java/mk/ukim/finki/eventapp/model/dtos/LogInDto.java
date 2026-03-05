package mk.ukim.finki.eventapp.model.dtos;


import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Data;
import mk.ukim.finki.eventapp.model.User;

@Data
@AllArgsConstructor
public class LogInDto {
    String email;
    String password;
}
