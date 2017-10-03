package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.payloads.CANMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A Linux SocketCAN API Adapter for Java.
 *
 * These requirements include promiscuous mode exposure of all bus traffic on all interfaces to the adapter,
 * treatment of Error frames, ability to transmit CAN packets, timestamping feature as provided by the SocketCAN API, etc.
 *
 * The implementation must be thread-safe, despite the fact that the Native adapter maintains singletons.
 *
 */
public class LinuxSocketCANAdapter {

    private static final Logger log = LogManager.getLogManager().getLogger(LinuxSocketCANAdapter.class.getName());
    private final NativeSocketCANAdapter nativeAdapter;
    private String gatewayId;

    public LinuxSocketCANAdapter() {
        System.loadLibrary("candora-native");
        nativeAdapter = new NativeSocketCANAdapter();
    }

    @PostConstruct
    public void init() throws CANException {
        nativeAdapter.init();
        log.log(Level.INFO, "Initialization of JNI Linux SocketCAN adapter successful.");
    }

    @PreDestroy
    public void close() throws CANException {
        nativeAdapter.close();
        log.log(Level.INFO, "Shutdown of JNI Linux SocketCAN adapter complete.");
    }

    /**
     * Set the socket receive timeout for the native adapter. When a non-zero timeout
     * is active, the receive() method will throw CANReceiveTimeoutException upon expiration
     * of the timeout within the kernel.
     *
     * Sets the receive timeout
     * @param timeout timeout in seconds, i.e. 0.010 means every 100 milliseconds. Precision to microseconds is honoured.
     */
    public void setReceiveTimeout(double timeout) {
        if (timeout < 0.0) {

        }
        double frac = timeout % 1;
        int intpart = (int)(timeout - frac);
        int microseconds = (int)(frac * 1E+6);
        assert microseconds <= 999999;
        // todo - tell the native member that our timeout is active.
    }


    /**
     * Blocking send
     * @param message Attempts to a message, either CAN 2.0 or CAN FD
     * @throws CANException any subclass of the this root exception
     */
    public void send(CANMessage message) throws CANException {
        NativeCANFrame f = new NativeCANFrame();
        nativeAdapter.send(f);
    }

    /**
     * Blocking receive, possibly receiving a CANErrorMessage or a normal payload.
     * @return Message Payload, may be an Error, or a CAN 2.0 or CAN FD payload.
     * @throws CANException any subclass of the this root exception
     */
    public CANMessage receive() throws CANException {
        NativeCANFrame f = nativeAdapter.receive();
        if (f.errFlag) {

        }

        if (f.effFlag) {

        } else {

        }

        throw new CANException("XXX TODO Implement me.");
    }



}
