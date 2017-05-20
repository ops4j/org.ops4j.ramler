package org.ops4j.ramler.exc;

/**
 * Base class for all exceptions thrown by Ramler.
 *
 * @author Harald Wellmann
 *
 */
public class RamlerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with the given message.
     * @param message message
     */
    public RamlerException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     * @param message message
     * @param cause cause
     */
    public RamlerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception with the given cause.
     * @param cause cause
     */
    public RamlerException(Throwable cause) {
        super(cause);
    }
}
