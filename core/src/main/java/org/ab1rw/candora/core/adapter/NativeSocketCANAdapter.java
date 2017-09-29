package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.payloads.CANMessage;

/**
 * Java Native (JNI) Proxy class.  This class is used by the Java Stub compiler (javah) to build
 * the C++ prototypes required for us to implement a native Linux SocketCAN adapter for the JVM.
 *
 * @see https://www.kernel.org/doc/Documentation/networking/can.txt
 */
class NativeSocketCANAdapter {

    public native String getVersionInfo();
    /**
     * Initialize the JNI adapter, before any other methods are called.
     */
    public native void init();

    /**
     * Closes the JNI adapter and releases any resources.
     */
    public native void close();

    public native void send(NativeCANFrame message) throws CANException;
    public native NativeCANFrame receive() throws CANException;
    public native NativeCANFrame poll() throws CANException;

}
