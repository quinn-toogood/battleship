package job.interview.exception;

/**
 * Error to be thrown when an invalid co-ordinate is supplied
 */
public class InvalidCoordinateException extends Exception {
    public InvalidCoordinateException(String message) {
        super(message);
    }
}
