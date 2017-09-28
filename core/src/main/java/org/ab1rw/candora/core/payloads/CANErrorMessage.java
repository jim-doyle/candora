package org.ab1rw.candora.core.payloads;

import org.ab1rw.candora.core.CANId;

public class CANErrorMessage extends CANMessage {
    public CANErrorMessage(String gatewayId, String interfaceId, CANId id, byte[] payload, long kernelTimeStamp) {
        super(gatewayId, interfaceId, id, payload, kernelTimeStamp);
    }

    // XXX TO DO - LARGE - Map all properties in /usr/include/linux/can/error.h as Java Enums

}
