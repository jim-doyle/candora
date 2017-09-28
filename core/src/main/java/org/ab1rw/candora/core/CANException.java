package org.ab1rw.candora.core;

/**
 * Linux SocketCAN Exception
 */
public class CANException extends Exception {
    public CANException(String msg) {
        super(msg);
    }
    public CANException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
