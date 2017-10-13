package org.ab1rw.candora.core;

/**
 * Base Exception Class for the Candora API.
 */
public class CANException extends Exception {

    public CANException(String msg) {
        super(msg);
    }
    public CANException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
