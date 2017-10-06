package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A CAN FD Message Payload
 */
public class CANFDMessage extends CANMessage implements Serializable {
    // CAN FD has a limited range of allowed payload widths, as follows
    private static final int [] allowedPayloadWidths = { 0,1,2,3,4,5,6,7,8,12,16,20,24,32,48,64 };

    protected final CANId id;
    protected final byte [] payload;

    /**
     * ctor for the message receive phase.
     * @param _gatewayId
     * @param _interfaceId
     * @param _id
     * @param _payload
     * @param _kernelTimeStamp
     */
    public CANFDMessage(String _gatewayId, String _interfaceId, CANId _id, byte[] _payload, long _kernelTimeStamp) {
        super(_gatewayId, _interfaceId, _kernelTimeStamp);
        id=_id;
        payload = _payload;
        checkPayloadLength();
    }

    /**
     * ctor to prepare a message for transmission
     * @param _id
     * @param _payload
     */
    public CANFDMessage(CANId _id, byte[] _payload) {
        super(null, null, -1);
        id=_id; payload=_payload;
        checkPayloadLength();
    }

    private void checkPayloadLength() {
        if (payload.length > 64) {
            throw new IllegalArgumentException("Invalid Message. CAN FD Message Payloads are limited to at most 64 bytes ; value given to constructor is "+payload.length+" bytes. ");
        }
        if (Arrays.binarySearch(allowedPayloadWidths, payload.length) < 0) {
            throw new IllegalArgumentException("Invalid payload requested. CAN FD Messages are limited to lengths of " + Arrays.asList(allowedPayloadWidths) + " : "+payload.length);
        }
    }

    public CANId getId() {
        return id;
    }

    public byte[] getPayload() {
        return payload;
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
