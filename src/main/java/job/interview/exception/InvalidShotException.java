package job.interview.exception;

/**
 * Error to be thrown when an invalid ship is attempted to be placed
 */
public class InvalidShotException extends Exception {
    public InvalidShotException(String message) {
        super(message);
    }

    public InvalidShotException(String message, Throwable cause) {
        super(message, cause);
    }
}
