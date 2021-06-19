package nl.daanh.hiromi.exceptions;

public class HiromiSettingNotFoundException extends RuntimeException {
    public HiromiSettingNotFoundException() {
        super();
    }

    public HiromiSettingNotFoundException(String message) {
        super(message);
    }

    public HiromiSettingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HiromiSettingNotFoundException(Throwable cause) {
        super(cause);
    }
}
