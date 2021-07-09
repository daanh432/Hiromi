package nl.daanh.hiromi.database;

public abstract class HiromiDatabaseException extends RuntimeException {
    public HiromiDatabaseException() {
        super();
    }

    public HiromiDatabaseException(Exception exception) {
        this(exception.getMessage(), exception.getCause());
    }

    public HiromiDatabaseException(String message) {
        super(message);
    }

    public HiromiDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiDatabaseException(Throwable cause) {
        super(cause);
    }
}
