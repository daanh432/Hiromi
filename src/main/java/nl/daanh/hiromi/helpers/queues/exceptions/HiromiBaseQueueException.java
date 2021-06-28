package nl.daanh.hiromi.helpers.queues.exceptions;

public class HiromiBaseQueueException extends RuntimeException {
    public HiromiBaseQueueException() {
        super();
    }

    public HiromiBaseQueueException(String message) {
        super(message);
    }

    public HiromiBaseQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiBaseQueueException(Throwable cause) {
        super(cause);
    }
}
