package nl.daanh.hiromi.database.disk;

import nl.daanh.hiromi.database.HiromiDatabaseException;

public class HiromiDiskIOException extends HiromiDatabaseException {
    public HiromiDiskIOException() {
        super();
    }

    public HiromiDiskIOException(Exception exception) {
        this(exception.getMessage(), exception.getCause());
    }

    public HiromiDiskIOException(String message) {
        super(message);
    }

    public HiromiDiskIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiDiskIOException(Throwable cause) {
        super(cause);
    }
}
