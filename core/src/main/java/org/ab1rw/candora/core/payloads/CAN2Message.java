package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.io.Serializable;
import java.util.Arrays;

/**
 * A CAN 2.0 Message Payload
 */
public class CAN2Message extends CANMessage implements Serializable {

    protected final CANId id;
    protected final byte [] payload;

    public CAN2Message(String _gatewayId, String _interfaceId, CANId _id, byte[] _payload, long _kernelTimeStamp) {
        super(_gatewayId, _interfaceId, _kernelTimeStamp);
        id=_id;
        payload = _payload;

        if (payload.length > 8) {
            throw new IllegalArgumentException("Invalid Message. CAN 2.0 Message Payloads are limited to 32 bits ; value given to constructor is "+_payload.length*8+" bits. ");
        }
    }

    public CAN2Message(CANId _id, byte[] _payload) {
        super(null, null, -1);
        id=_id; payload=_payload;
    }

    public CANId getId() {
        return id;
    }

    public byte[] getPayload() {
        return payload;
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
