package pmb.my.starter.exception;

/**
 * Runtime exception of the application, for scarce or small exceptions.
 *
 * @see RuntimeException
 */
public class MinorException
    extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message.
     *
     * @param message error message
     * @see RuntimeException#RuntimeException(String)
     */
    public MinorException(String message) {
        super(message);
    }

    /**
     * Constructor with message and throwable.
     *
     * @param message error message
     * @param throwable error cause
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public MinorException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**

    /**
     * Empty constructor.
     *
     * @see RuntimeException#RuntimeException()
     */
    public MinorException() {
        super();
    }

}
