package mk.ukim.finki.eventapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;




@SpringBootApplication
@EnableJpaRepositories("mk.ukim.finki.eventapp.repository")
@EntityScan("mk.ukim.finki.eventapp.model")
public class EventAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventAppApplication.class, args);
	}

}
