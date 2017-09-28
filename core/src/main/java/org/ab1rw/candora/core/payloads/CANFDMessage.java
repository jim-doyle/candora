package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;

public class CANFDMessage extends CANMessage {
    public CANFDMessage(String gatewayId, String interfaceId, CANId id, byte[] payload, long kernelTimeStamp) {
        super(gatewayId, interfaceId, id, payload, kernelTimeStamp);
        // XXX todo - assert 64 byte DLC limit on payload!
    }
}
