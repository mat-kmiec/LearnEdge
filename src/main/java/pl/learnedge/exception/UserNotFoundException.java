package pl.learnedge.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Nie ma takiego użytkownika");
    }
    
    public UserNotFoundException(String message) {
        super(message);
    }
}
