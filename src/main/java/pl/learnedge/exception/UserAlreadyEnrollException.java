package pl.learnedge.exception;

public class UserAlreadyEnrollException extends RuntimeException{
    public UserAlreadyEnrollException(){
        super("Użytkownik jest już przypisany do kursu!");
    }
}
