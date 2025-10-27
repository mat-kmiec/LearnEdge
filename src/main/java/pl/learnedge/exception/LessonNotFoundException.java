package pl.learnedge.exception;

public class LessonNotFoundException extends RuntimeException  {
    public LessonNotFoundException() {
        super("Nie odnaleziono lekcji!");
    }
}
