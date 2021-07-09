package nl.daanh.hiromi.database.api;

import nl.daanh.hiromi.database.HiromiDatabaseException;

public class HiromiApiIOException extends HiromiDatabaseException {
    public HiromiApiIOException() {
        super();
    }

    public HiromiApiIOException(Exception exception) {
        super(exception);
    }

    public HiromiApiIOException(String message) {
        super(message);
    }

    public HiromiApiIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiApiIOException(Throwable cause) {
        super(cause);
    }
}
