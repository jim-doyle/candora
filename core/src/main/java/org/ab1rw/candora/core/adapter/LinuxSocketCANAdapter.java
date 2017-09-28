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

    public enum speed { S250Kpbs, S500Kbps, s1000Kbps };
    private String gatewayId;

    public void init() throws CANException {
        log.log(Level.INFO, "Initialization of JNI Linux SocketCAN adapter successful.");
    }
    public void close() throws CANException {
        log.log(Level.INFO, "Shutdown of JNI Linux SocketCAN adapter complete.");
    }

    /**
     * Blocking send
     * @param message
     * @throws CANException
     */
    public void send(CANMessage message) throws CANException {
        throw new CANException("XXX TODO Implement me.");
    }

    /**
     * Blocking receive, possibly receiving a CANErrorMessage or a normal payload.
     * @return
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