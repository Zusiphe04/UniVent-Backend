package za.ac.cput.exception;

public class StaffNumberAlreadyExistsException extends RuntimeException {
    public StaffNumberAlreadyExistsException(String message) {
        super(message);
    }
}
