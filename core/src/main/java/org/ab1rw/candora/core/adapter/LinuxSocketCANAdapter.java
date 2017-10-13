package org.ab1rw.candora.core.adapter;

import org.ab1rw.candora.core.CANAdapterException;
import org.ab1rw.candora.core.CANException;
import org.ab1rw.candora.core.payloads.CANId;
import org.ab1rw.candora.core.payloads.CAN2Message;
import org.ab1rw.candora.core.payloads.CANErrorMessage;
import org.ab1rw.candora.core.payloads.CANFDMessage;
import org.ab1rw.candora.core.payloads.CANMessage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.logging.Level;
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

    /**
     * Create an adapter using Java Native (JNI) methods to a Linux SocketCAN proxy.
     * @param gatewayId i.e. myhost.domain.org
     * @param interfaceId i.e. can0
     * @throws CANAdapterException CANException usuall any adapter (socket) specific cause
     */
    public LinuxSocketCANAdapter(String gatewayId, String interfaceId) throws CANAdapterException {
        try {
            System.loadLibrary("candora-jni");
        } catch (UnsatisfiedLinkError ule) {
            log.log(Level.SEVERE, "", ule);
            throw new CANAdapterException("Error loading libcandora-jni.so. Set LD_LIBRARY_PATH or use -Djava.library.path, " +
                    "check for for existence and access permissions.",ule);
        }

        nativeAdapter = new NativeSocketCANAdapter();
        this.gatewayId = gatewayId;
        nativeAdapter.setInterfaceId(interfaceId);
    }

    /**
     * Activate the adapter prior to send or receive operations.
     * @throws CANException CANException usuall any adapter (socket) specific cause
     */
    @PostConstruct
    public void init() throws CANException {
        if (gatewayId == null || gatewayId.trim().isEmpty()) throw new CANAdapterException("Adapter gateway ID property must be assigned a non-null, non-blank value.");
        if (nativeAdapter.getInterfaceId() == null) throw new CANAdapterException("Adapter interface property must be assigned (i.e. can0) ");
        log.log(Level.FINE,"Entering native code.");
        nativeAdapter.init();
        log.log(Level.FINE,"Returned from native code.");
        log.log(Level.INFO, "Initialization of JNI Linux SocketCAN adapter successful on interface {0} as adapter {1} ", new Object [] {nativeAdapter.getInterfaceId(), gatewayId});
    }

    /**
     * Passivates the adapter, releasing LinuxCAN resources in the C++ native coded if needed.
     * @throws CANException usuall any adapter (socket) specific cause
     */
    @PreDestroy
    public void close() throws CANException {
        log.log(Level.FINE,"Entering native code.");
        nativeAdapter.close();
        log.log(Level.FINE,"Returned from native code.");
        log.log(Level.INFO, "Shutdown of JNI Linux SocketCAN adapter complete.");
    }

    /**
     * For configuration, Set the socket receive timeout for the native adapter. When a non-zero timeout
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
     * For configuration, If set, the receive(...) method can return CANErrorMessage instances, as well as CANFDMessages.
     * @param arg enables receipt of bus and controller error event messages
     */
    void setErrorFramesEnabled(boolean arg) {
        nativeAdapter.setErrorFramesEnabled(arg);
    }

    /**
     * Creates a CAN FD Message suitable for use with the send() method, selecting a CAN FD frame length and padding as needed.
     * @param id CAN bus ID.
     * @param payload payload
     * @param pad pad bytes, to fill the unused positions in the frame when the given payload
     *            does not completely fit one of the 16 permitted sizes.
     * @return CAN FD message
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
     * @param arg address
     * @param payload payload bytes
     * @return CAN 2.0 message
     */
    public CAN2Message create(CANId arg, byte [] payload) {
        throw new RuntimeException("XXX Implement me.");
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
            // off to our dedicated error message class to handle deciphering the bitwise data.
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

        // BRS: if ((f.can_fd_flags & 0x01) > 0)
        // ESI: if ((f.can_fd_flags & 0x02) > 0)

        return new CANFDMessage(gatewayId, f.canInterface, id, f.can_data,  f.timestamp, f.rtrFlag, (f.can_fd_flags & 0x01)>0, (f.can_fd_flags & 0x02) > 0);
    }
}
