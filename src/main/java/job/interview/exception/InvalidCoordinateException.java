package job.interview.exception;

/**
 * Error to be thrown when an invalid ship is attempted to be placed
 */
public class InvalidCoordinateException extends Exception {
    public InvalidCoordinateException(String message) {
        super(message);
    }

    public InvalidCoordinateException(String message, Throwable cause) {
        super(message, cause);
    }
}
