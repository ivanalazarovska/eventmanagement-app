package mk.ukim.finki.eventapp.model.exceptions;

public class InvalidUserCredentialsException extends RuntimeException{
    public InvalidUserCredentialsException() {
        super("Wrong username or password");
    }
}
