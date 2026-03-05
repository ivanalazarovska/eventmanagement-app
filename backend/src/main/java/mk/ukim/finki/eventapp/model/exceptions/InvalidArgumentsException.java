package mk.ukim.finki.eventapp.model.exceptions;


public class InvalidArgumentsException extends RuntimeException{
    public InvalidArgumentsException() {
        super("Please enter all data");
    }
}