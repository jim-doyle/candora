package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANAdapterException;
import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.payloads.CANMessage;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Java Native (JNI) Proxy class.  This class is used by the Java Stub compiler (javah) to build
 * the C++ prototypes required for us to implement a native Linux SocketCAN adapter for the JVM.
 *
 * @see https://www.kernel.org/doc/Documentation/networking/can.txt
 */
// XXX to do - make the package scoped when the JNI work is finished.
public class NativeSocketCANAdapter {

    private final boolean recvErrorFrames;
    private final int recvTimeoutSeconds;
    private final int recvTimeoutMicroseconds;
    private final static Logger nativeLogger = Logger.getLogger("Foo");
 
    private static Long testing = new Long(1);
    private int socket;

    private long lastReceivedTimestamp;
    private long lastErrorReceivedTimestamp;
    private long lastSentTimestamp;
    private boolean adapterReady;

    public NativeSocketCANAdapter(boolean exposeErrorFrames, double recvTimeout) {

	nativeLogger.info("kickstart");
        recvErrorFrames = exposeErrorFrames;
        if (recvTimeout < 0.0) {
            throw new IllegalArgumentException("Ctor: Receive timeout argument must be zero or a positive number in seconds.");
        }
        // convert to seconds and microseconds for struct timeval in C api
        double frac = recvTimeout % 1;
        recvTimeoutSeconds = (int)(recvTimeout - frac);
        recvTimeoutMicroseconds = (int)(frac * 1E+6);
        assert recvTimeoutMicroseconds <= 999999;
    }

    /**
     * Retrieves the build information string from teh native componen
     * @return
     */
    public native String getVersionInfo();
    /**
     * Initialize the JNI adapter, before any other methods are called.
     */
    public native synchronized void init() throws CANAdapterException;

    /**
     * Closes the JNI adapter and releases any resources.
     */
    public native synchronized void close() throws CANAdapterException;
    public native synchronized void send(NativeCANFrame message) throws CANAdapterException;
    public native synchronized NativeCANFrame receive() throws CANAdapterException;

}
