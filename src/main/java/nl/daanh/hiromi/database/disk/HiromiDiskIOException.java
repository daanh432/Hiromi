package nl.daanh.hiromi.database.disk;

public class HiromiDiskIOException extends RuntimeException {
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
