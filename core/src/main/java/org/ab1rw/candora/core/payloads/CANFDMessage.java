package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;

/**
 * A CAN FD Message Payload
 */
public class CANFDMessage extends CANMessage {
    public CANFDMessage(String _gatewayId, String _interfaceId, CANId _id, byte[] _payload, long _kernelTimeStamp) {
        super(_gatewayId, _interfaceId, _id, _payload, _kernelTimeStamp);
        if (payload.length > 64) {
            throw new IllegalArgumentException("Invalid Message. CAN FD Message Payloads are limited to 64 bytes ; value given to construct is "+_payload.length+" bytes. ");
        }
    }
}
