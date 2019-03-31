package org.ops4j.ramler.common.exc;

/**
 * Exception caused by Ramler during code or HTML generation.
 *
 * @author Harald Wellmann
 *
 */
public class GeneratorException extends RamlerException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with the given message.
     * @param message message
     */
    public GeneratorException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     * @param message message
     * @param cause cause
     */
    public GeneratorException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception with the given cause.
     * @param cause cause
     */
    public GeneratorException(Throwable cause) {
        super(cause);
    }
}
