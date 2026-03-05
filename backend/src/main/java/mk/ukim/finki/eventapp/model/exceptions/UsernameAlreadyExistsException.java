package mk.ukim.finki.eventapp.model.exceptions;


public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username) {
        super(String.format("User with %s username already exist", username));
    }
}
