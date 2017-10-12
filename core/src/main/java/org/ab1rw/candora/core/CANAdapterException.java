package org.ab1rw.candora.core;

/**
 * A CAN Adapter exception is an failure event related to the Linux SocketCAN protocol
 * stack,  not the physical conditions of the CAN bus itself.  For the most part, socket
 * facility errors are reported along with this exception.
 *
 */
public class CANAdapterException extends CANException {

    private static final long serialVersionUID = 1L;

    private int errno;  // unix errno reported
    private String nativeAdapterDebugInformation;

    public CANAdapterException(String arg) {
        super(arg);
    }

    public CANAdapterException(String arg1, Throwable arg2) {
        super(arg1, arg2);
    }

}
