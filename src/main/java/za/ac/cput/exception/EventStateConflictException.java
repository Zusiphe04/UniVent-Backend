package za.ac.cput.exception;

public class EventStateConflictException extends RuntimeException {
    public EventStateConflictException(String message) {
        super(message);
    }
}
