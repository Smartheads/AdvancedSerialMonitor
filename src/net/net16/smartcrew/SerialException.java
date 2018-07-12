package net.net16.smartcrew;

/**
 *
 * @author rohu7
 */
public class SerialException extends Exception {

    /**
     * Creates a new instance of <code>SerialException</code> without detail
     * message.
     */
    public SerialException() {
    }

    /**
     * Constructs an instance of <code>SerialException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public SerialException(String msg) {
        super(msg);
    }
}
