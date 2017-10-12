package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A CAN 2.0 Message Payload
 * @see CANFDMessage
 */
public class CAN2Message extends CANMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final CANId id;
    protected final byte [] payload;

    public CAN2Message(String _gatewayId, String _interfaceId, CANId _id, byte[] _payload, long _kernelTimeStamp) {
        super(_gatewayId, _interfaceId, _kernelTimeStamp);
        if (_id == null || _payload == null) throw new IllegalArgumentException("ctor: neither can ID nor payload argument can be null valued.");
        id=_id;
        payload = _payload;
        checkPayloadLength();
    }

    public CAN2Message(CANId _id, byte[] _payload) {
        super(null, null, -1);
        if (_id == null || _payload == null) throw new IllegalArgumentException("ctor: neither can ID nor payload argument can be null valued.");
        id=_id; payload=_payload;
        checkPayloadLength();
    }

    private void checkPayloadLength() {
        if (payload.length > 8) {
            throw new IllegalArgumentException("Invalid payload size. CAN 2.0 Messages are limited to lengths < 8 bytes : "+payload.length);
        }
    }

    public CANId getId() {
        return id;
    }

    public byte[] getPayload() {
        // protect immutability of the message, returns a copy
        return Arrays.copyOf(payload, payload.length);
    }

    @Override
    public String toString() {
        return "CAN2Message{" +
                "id=" + id.toString() +
                ", payload=" + payloadToStringHelper(payload) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CAN2Message that = (CAN2Message) o;

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
