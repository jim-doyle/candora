package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.io.Serializable;
import java.util.Arrays;
/**
 * A CAN FD Message Payload
 * @see CANMessage
 */
public class CANFDMessage extends CANMessage implements Serializable {
    // CAN FD has a limited range of allowed payload widths, as follows
    private static final int [] allowedPayloadWidths = { 0,1,2,3,4,5,6,7,8,12,16,20,24,32,48,64 };

    protected final CANId id;
    protected final byte [] payload;

    /**
     * ctor for the message receive phase by the adapter.
     * @param _gatewayId the arbitrary gateway ID where this CAN message arrived from.
     * @param _interfaceId the can interface ID on the gateway machine where this message appeared.
     * @param _id can address
     * @param _payload wire payload
     * @param _kernelTimeStamp The Linux SocketCAN receive timestamp (from SO_RCVTIMEO ioctl)
     * @throws IllegalArgumentException if an unsupported message payload length is given
     */
    public CANFDMessage(String _gatewayId, String _interfaceId, CANId _id, byte[] _payload, long _kernelTimeStamp) {
        super(_gatewayId, _interfaceId, _kernelTimeStamp);
        id=_id;
        payload = _payload;
        if (_id == null || _payload == null) throw new IllegalArgumentException("ctor: neither can ID nor payload argument can be null valued.");
        checkPayloadLength();
    }

    /**
     * ctor to prepare a message for transmission by user code.
     * @param _id CAN id (SFF or EFF)
     * @param _payload Message payload, restricted by CAN FD specification lengths.
     * @throws IllegalArgumentException if an unsupported message payload length is given
     */
    public CANFDMessage(CANId _id, byte[] _payload) {
        super(null, null, -1);
        if (_id == null || _payload == null) throw new IllegalArgumentException("ctor: neither can ID nor payload argument can be null valued.");
        id=_id; payload=_payload;
        checkPayloadLength();
    }

    private void checkPayloadLength() {
        if (Arrays.binarySearch(allowedPayloadWidths, payload.length) < 0) {
            throw new IllegalArgumentException("Invalid payload size. CAN FD Messages are limited to lengths of " + Arrays.asList(allowedPayloadWidths) + " : "+payload.length);
        }
    }

    public CANId getId() {
        return id;
    }

    public byte[] getPayload() {
        // protect immutability of the message, returns a copy
        return Arrays.copyOf(payload, payload.length);
    }

    public int getPayloadLength() {
        return payload.length;
	}


    @Override
    public String toString() {
        return "CANFDMessage{" +
                "id=" + id.toString() +
                ", payload=" + payloadToStringHelper(payload) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CANFDMessage that = (CANFDMessage) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(payload);
        return result;
    }
}
