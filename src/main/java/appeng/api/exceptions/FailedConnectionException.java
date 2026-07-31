package appeng.api.exceptions;

public class FailedConnectionException extends Exception {

    public FailedConnectionException() {
    }

    public FailedConnectionException(final String message) {
        super(message);
    }

    public FailedConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
