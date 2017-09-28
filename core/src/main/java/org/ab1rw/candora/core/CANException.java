package org.ab1rw.candora.core;

/**
 * Base Exception for the Candora API - an adapter for Java to the Linux SocketCAN API.
 */
public class CANException extends Exception {
    public CANException(String msg) {
        super(msg);
    }
    public CANException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
