package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANAdapterException;
import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.payloads.CANMessage;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Java Native (JNI) Adapter proxy.  This class is used by the Java Stub compiler (javah) to build
 * the C++ prototypes required for us to implement a native Linux SocketCAN adapter. This class
 * should be package scoped to prevent its exposure and discourage direct use outside of his java package.
 *
 *
 * Note that all attribute names, and types are coupled to C code in the nativeapi module.
 * If you change signature here, you _must_ reflect the same changes in the C code, and
 * vice versa.
 *
 * see Linux http://www.kernel.org/doc/Documentation/networking/can.txt
 */
// XXX to do - make the package scoped when the JNI work is finished.
public class NativeSocketCANAdapter {

    private final static Logger nativeLogger = Logger.getLogger(NativeSocketCANAdapter.class.getName());

    // these private members are manipulated by the C native library
    private final boolean recvErrorFrames;
    private final int recvTimeoutSeconds;
    private final int recvTimeoutMicroseconds;

    private static Long testing = new Long(1);
    private int socket;
    private boolean adapterReady;

    public NativeSocketCANAdapter(boolean exposeErrorFrames, double recvTimeout) {
        socket = -1;

	    nativeLogger.info("kickstart");

        recvErrorFrames = exposeErrorFrames;
        if (recvTimeout < 0.0) {
            throw new IllegalArgumentException("Ctor: Receive timeout argument must be zero or a positive number in seconds.");
        }
        // convert to seconds and microseconds for struct timeval in C api
        double frac = recvTimeout % 1;
        recvTimeoutSeconds = (int)(recvTimeout - frac);
        recvTimeoutMicroseconds = (int)(frac * 1E+6);
        assert recvTimeoutMicroseconds <= 999999; // linux kernel will reject microseconds > 999999
    }


    public final native String getVersionInfo();
    public final native synchronized void init() throws CANAdapterException;
    public final native synchronized void close() throws CANAdapterException;
    public final native synchronized void send(NativeCANFrame message) throws CANAdapterException;
    public final native synchronized void receive(NativeCANFrame message) throws CANAdapterException;

}
