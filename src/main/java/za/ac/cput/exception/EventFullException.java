package za.ac.cput.exception;

public class EventFullException extends IllegalStateException {
    public EventFullException(String message) {
        super(message);
    }
}
