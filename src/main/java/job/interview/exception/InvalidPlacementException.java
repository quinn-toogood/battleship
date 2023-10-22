package job.interview.exception;

/**
 * Error to be thrown when an invalid ship is attempted to be placed
 */
public class InvalidPlacementException extends Exception {
    public InvalidPlacementException(String message) {
        super(message);
    }

    public InvalidPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}
