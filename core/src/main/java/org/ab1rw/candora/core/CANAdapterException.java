package org.ab1rw.candora.core;

/**
 * A CAN Adapter exception is an failure event related to the Linux SocketCAN protocol
 * stack,  not the physical conditions of the CAN bus itself.  For the most part, socket
 * facility errors are reported along with this exception.
 *
 */
public class CANAdapterException extends CANException {
    private int errno;  // unix errno reported
    private String nativeAdapterDebugInformation;   // file, linenumber.

    public CANAdapterException(String arg) {
        super(arg);
    }

    public CANAdapterException() {
        super("JNI layer fault.");
    }

}
