package org.ops4j.ramler.exc;

/**
 * Wraps a syntax exception caused by the RAML parser or by Ramler itself.
 *
 * @author Harald Wellmann
 *
 */
public class ParserException extends RamlerException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with the given message.
     * @param message message
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given message and cause.
     * @param message message
     * @param cause cause
     */
    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception with the given cause.
     * @param cause cause
     */
    public ParserException(Throwable cause) {
        super(cause);
    }
}
