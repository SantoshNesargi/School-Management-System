package Students.util;

/**
 * Thrown by {@link ValidationUtil} when a request parameter is missing or
 * malformed. Carries an HTTP-style status (default 400) and a user-safe
 * message that the servlet layer writes straight into the JSON response.
 */
public class BadRequestException extends RuntimeException {

    private final int status;

    public BadRequestException(String message) {
        this(400, message);
    }

    public BadRequestException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
