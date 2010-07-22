package pt.inevo.encontra.nbtree.exceptions;

/**
 * Generic NBTree exception.
 * @author Ricardo Dias
 */
public class NBTreeException extends Exception {

    private static final long serialVersionUID = -4021379866894520183L;

    /**
     * Basic constructor.
     */
    public NBTreeException() {
        super();
    }

    /**
     * Constructor with message.
     *
     * @param message Error message.
     */
    public NBTreeException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message Error message.
     * @param cause Cause of exception.
     */
    public NBTreeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause.
     *
     * @param cause Cause of exception.
     */
    public NBTreeException(final Throwable cause) {
        super(cause);
    }
}