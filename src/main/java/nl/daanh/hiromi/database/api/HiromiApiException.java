package nl.daanh.hiromi.database.api;

import nl.daanh.hiromi.database.HiromiDatabaseException;

public class HiromiApiException extends HiromiDatabaseException {
    public HiromiApiException() {
        super();
    }

    public HiromiApiException(Exception exception) {
        super(exception);
    }

    public HiromiApiException(String message) {
        super(message);
    }

    public HiromiApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiApiException(Throwable cause) {
        super(cause);
    }
}
