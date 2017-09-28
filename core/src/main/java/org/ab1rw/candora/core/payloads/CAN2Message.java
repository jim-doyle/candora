package org.ab1rw.candora.core.payloads;

import org.ab1rw.candora.core.CANId;

public class CAN2Message extends CANMessage {
    public CAN2Message(String gatewayId, String interfaceId, CANId id, byte[] payload, long kernelTimeStamp) {
        super(gatewayId, interfaceId, id, payload, kernelTimeStamp);
        // XXX todo - assert 8 byte DLC limit on payload!
    }
}
