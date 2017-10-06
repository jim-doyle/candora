package org.ab1rw.candora.core.payloads;

import java.io.Serializable;

/**
 * CAN Message base class.
 * Note that this is an immutable object.
 */
public abstract class CANMessage implements Serializable {

    protected final String gatewayId;
    protected final String interfaceId;
    protected final long kernelTimeStamp;

    public CANMessage(String gatewayId, String interfaceId, long kernelTimeStamp) {
        this.gatewayId = gatewayId;
        this.interfaceId = interfaceId;
        this.kernelTimeStamp = kernelTimeStamp;
        assert gatewayId != null && ! gatewayId.isEmpty();
        assert interfaceId != null & ! interfaceId.isEmpty();
        assert kernelTimeStamp > 0;
    }

    /**
     * For messages received by native code, identifies the gateway instance where the message was received.
     * @return i.e. "localhost", may be null.
     */
    public String getGatewayId() {
        return gatewayId;
    }

    /**
     * Identifies the can interface on the gateway node where this message received or must go out from.
     * @return i.e. "can0"
     */
    public String getInterfaceId() {
        return interfaceId;
    }

    static String payloadToStringHelper(byte [] arg) {
        StringBuffer tmp = new StringBuffer();
        if (arg.length > 0) tmp.append("0x");
        for (byte b : arg) {
            tmp.append(String.format("%02x",b));
            tmp.append(" ");
        }
        return tmp.toString();
    }

}
