package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;

/**
 * A CAN FD Message Payload
 */
public class CANFDMessage extends CANMessage {

    protected final CANId id;
    protected final byte [] payload;

    public CANFDMessage(String _gatewayId, String _interfaceId, CANId _id, byte[] _payload, long _kernelTimeStamp) {
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

    public int getPayloadLength() {
	return payload.length;
	}


}
