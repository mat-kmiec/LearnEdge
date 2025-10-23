package pl.learnedge.exception;

public class EmailAlreadyTakenException extends RuntimeException{
    public EmailAlreadyTakenException() {
        super("Email jest już zajęty");
    }
}
