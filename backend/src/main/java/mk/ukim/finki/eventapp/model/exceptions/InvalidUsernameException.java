package mk.ukim.finki.eventapp.model.exceptions;

public class InvalidUsernameException extends RuntimeException{
    public InvalidUsernameException(String username) {
        super(String.format("Username %s doesn't exist", username));
    }
}
