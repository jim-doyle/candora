package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.payloads.CANMessage;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * A Linux SocketCAN API Adapter for Java that meets the requirements for Candora.
 * These requirements include promiscuous mode exposure of all bus traffic on all interfaces to the adapter,
 * treatment of Error frames, ability to transmit CAN packets, timestamping feature as provided by the SocketCAN API, etc.
 *
 * The implementation must be thread-safe, despite the fact that the Native adapter maintains singletons.
 *
 */
public class LinuxSocketCANAdapter {

    private static final Logger log = LogManager.getLogManager().getLogger(LinuxSocketCANAdapter.class.getName());
    private String gatewayId;

    public void init() throws CANException {
        log.log(Level.INFO, "Initialization of JNI Linux SocketCAN adapter successful.");
    }
    public void close() throws CANException {
        log.log(Level.INFO, "Shutdown of JNI Linux SocketCAN adapter complete.");
    }

    /**
     * Set the socket receive timeout for the native adapter. When a non-zero timeout
     * is active, the receive() method will throw CANReceiveTimeoutException upon expiration
     * of the timeout within the kernel.
     *
     * Sets the receive timeout
     * @param timeout timeout in seconds, precision to microseconds is honoured.
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
     * @param message
     * @throws CANException
     */
    public void send(CANMessage message) throws CANException {
        // call the native adapter...
        throw new CANException("XXX TODO Implement me.");
    }

    /**
     * Blocking receive, possibly receiving a CANErrorMessage or a normal payload.
     * @return
     * @throws
     * @throws CANException
     */
    public CANMessage receive() throws CANException {
        // Design note, a timeout may occur on a blocking receive.
        throw new CANException("XXX TODO Implement me.");
    }

    /**
     * Nonblocking receive, possibly receiving a CANErrorMessage or a normal payload.
     * @return null if no CANbus message is available
     * @throws CANException
     */
    public CANMessage poll() throws CANException {
        throw new CANException("XXX TODO Implement me.");
    }

}
