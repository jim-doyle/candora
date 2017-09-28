package org.ab1rw.candora.core.adapter;

/**
 * Java Native (JNI) Proxy class.  This class is used by the Java Stub compiler (javah) to build
 * the C++ prototypes required for us to implement a native Linux SocketCAN adapter for the JVM.
 */
class NativeSocketCANAdapter {

    /**
     * Initialize the JNI adapter, before any other methods are called.
     */
    public native void init();

    /**
     * Closes the JNI adapter and releases any resources.
     */
    public native void close();

}
