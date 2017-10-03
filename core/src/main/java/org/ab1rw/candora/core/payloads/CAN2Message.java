package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;

/**
 * A CAN 2.0 Message Payload
 */
public class CAN2Message extends CANMessage {
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

    public CANId getId() {
        return id;
    }

    public byte[] getPayload() {
        return payload;
    }

}
