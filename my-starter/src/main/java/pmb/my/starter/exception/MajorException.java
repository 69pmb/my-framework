package pmb.my.starter.exception;

/**
 * Checked exception of the application, for usual or big exceptions.
 *
 * @see Exception
 */
public class MajorException
    extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor with message.
     *
     * @param message error message
     * @see Exception#Exception(String)
     */
    public MajorException(String message) {
        super(message);
    }

    /**
     * Constructor with message and throwable.
     *
     * @param message error message
     * @param throwable error cause
     * @see Exception#Exception(String, Throwable)
     */
    public MajorException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Empty constructor.
     *
     * @see Exception#Exception()
     */
    public MajorException() {
        super();
    }

}
