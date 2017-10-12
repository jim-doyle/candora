package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANAdapterException;
import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.CANId;
import org.ab1rw.candora.core.payloads.*;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
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

    private static final Logger log = Logger.getLogger(LinuxSocketCANAdapter.class.getName());
    private final NativeSocketCANAdapter nativeAdapter;
    private String gatewayId;

    public LinuxSocketCANAdapter() throws CANAdapterException {
        try {
            System.loadLibrary("candora-native");
        } catch (UnsatisfiedLinkError ule) {
            log.log(Level.SEVERE, "", ule);
            throw new CANAdapterException("Error loading candora-native.so. Set LD_LIBRARY_PATH or use -Djava.library.path, " +
                    "check for for existence and access permissions.",ule);
        }
        nativeAdapter = new NativeSocketCANAdapter();
    }

    @PostConstruct
    public void init() throws CANException {
        log.log(Level.FINE,"Entering native code.");
        nativeAdapter.init();
        log.log(Level.FINE,"Returned from native code.");
        log.log(Level.INFO, "Initialization of JNI Linux SocketCAN adapter successful.");
    }

    @PreDestroy
    public void close() throws CANException {
        log.log(Level.FINE,"Entering native code.");
        nativeAdapter.close();
        log.log(Level.FINE,"Returned from native code.");
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
       nativeAdapter.setRecvTimeout(timeout);
    }

    /**
     * Setup the adapter to use a specific interface on the host,
     * @param arg i.e. "can1" if this adapter instance will only send and receive to can1
     */
    void setInterfaceId(String arg) {
        nativeAdapter.setInterfaceId(arg);
    }

    /**
     * Setup the adapter to receive from any interface on the host, and send a message out
     * on ALL interfaces.
     */
    void setAllInterfaces() {
        nativeAdapter.setAllInterfaces();
    }

    /**
     * If set, the receive(...) method can return CANErrorMessage instances, as well as CANFDMessages.
     * @param arg enables receipt of bus and controller error event messages
     */
    void setErrorFramesEnabled(boolean arg) {
        nativeAdapter.setErrorFramesEnabled(arg);
    }

    /**
     * Creates a CAN FD Message suitable for use with the send() method.
     * @param id CAN bus ID.
     * @param payload payload
     * @param pad pad bytes, to fill the unused positions in the frame when the given payload
     *            does not completely fit one of the 16 permitted sizes.
     * @return
     */
    public CANFDMessage create(CANId id, byte [] payload, byte pad) {

        // find the least possible CAN FD dlc that will fit this payload.
        int [] allowedPayloadWidths = { 0,1,2,3,4,5,6,7,8,12,16,20,24,32,48,64 };
        int i=0;
        int use=0;
        while (i<allowedPayloadWidths.length) {
            if (allowedPayloadWidths[i] == payload.length) {
                use=i;
                break;
            }
            if (allowedPayloadWidths[i] < payload.length && allowedPayloadWidths[i+1] > payload.length) {
                use = i + 1;
                break;
            }
            else i++;
        }

        //
        byte [] p = new byte[allowedPayloadWidths[use]];
        if (allowedPayloadWidths[use] > payload.length) {
            Arrays.fill(p, payload.length, allowedPayloadWidths[use], pad);
        }
        return null;
    }

    /**
     * Creates a CAN 2.0 Message suitable for use with the send() method
     * @param arg
     * @param payload
     * @return
     */
    public CAN2Message create(CANId arg, byte [] payload) {
        return null;
    }

    /**
     * Blocking message send
     * @param message Attempts to a message, either CAN 2.0 or CAN FD
     * @throws CANException any subclass of the this root exception
     */
    public void send(CANFDMessage message) throws CANException {

        NativeCANFrame f = new NativeCANFrame();
        f.can_data = message.getPayload();
        f.can_dlc = (byte) message.getPayloadLength();
        f.can_id = message.getId().getBits();

        log.log(Level.FINE,"Entering native code.");
        nativeAdapter.send(f);
        log.log(Level.FINE,"Returned from native code.");
        log.log(Level.INFO,"Sent message {0}", new Object [] {message});
    }

    /**
     * Blocking receive, possibly receiving a CANErrorMessage or a normal payload.
     * @return Message Payload, may be an Error, or a CAN 2.0 or CAN FD payload.
     * @throws CANException any subclass of the this root exception
     */
    public CANMessage receive() throws CANException {
        NativeCANFrame f =  new NativeCANFrame();
        log.log(Level.FINE,"Entering native code.");
        nativeAdapter.receive(f);
        log.log(Level.FINE,"Returned from native code.");


        if (f.errFlag) {
            // in this case, the error details are hiding in the can id bits; delegate the construction
            // off to our dedicated error message payload
            log.log(Level.WARNING,"CAN bus error observed on {0}", new Object [] { f.canInterface });
            return new CANErrorMessage(gatewayId, f.canInterface, f.can_id, f.can_data, f.timestamp);
        }

        // Not an error frame, then construct an immutable value object to return to the caller.
        final CANId id;
        // determine if we are using 11 or 29 bit bus addresses, and appropriately mask off bits
        if (f.effFlag) {
            id = new CANId(f.can_id & 0x1FFFFFFF);  /* CAN_EFF_MASK - 29 bit address */
        } else {
            id = new CANId(f.can_id & 0x7FF);       /* CAN_SFF_MASK - 11 bit address */
        }

        return new CANFDMessage(gatewayId, f.canInterface, id, f.can_data,  f.timestamp);
    }
}
