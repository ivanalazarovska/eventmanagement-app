package mk.ukim.finki.eventapp.model.dtos;

import lombok.Data;
import mk.ukim.finki.eventapp.model.enumerations.Role;

@Data
public class RegisterDto {
    String username;
    String email;
    String password;
    String repeatPassword;
    String name;
    String surname;
    Role role;
}
