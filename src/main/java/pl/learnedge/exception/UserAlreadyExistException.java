package pl.learnedge.exception;

public class UserAlreadyExistException extends RuntimeException{
    public UserAlreadyExistException() {
        super("Użytkownik o takiej nazwie już istnieje");
    }
}
