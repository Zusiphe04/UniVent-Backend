package za.ac.cput.exception;

public class AlreadyBookedException extends IllegalStateException {
    public AlreadyBookedException(String message) {
        super(message);
    }
}
