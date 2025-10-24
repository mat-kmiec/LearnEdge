package pl.learnedge.exception;

public class CourseNotFoundException extends RuntimeException{
    public CourseNotFoundException(){
        super("Nie odnaleziono kursu z podanym slugiem");
    }
}
