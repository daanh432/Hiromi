package nl.daanh.hiromi.helpers.queues.exceptions;

public class HiromiQueueIOException extends RuntimeException {
    public HiromiQueueIOException() {
        super();
    }

    public HiromiQueueIOException(String message) {
        super(message);
    }

    public HiromiQueueIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiQueueIOException(Throwable cause) {
        super(cause);
    }
}
