package org.ab1rw.candora.core.payloads;

import java.io.Serializable;
import java.util.Optional;

/**
 * CAN Message base class.
 * Note that this is an immutable object.
 */
public abstract class CANMessage implements Serializable {


    protected final Optional<String> gatewayId;
    protected final Optional<String> interfaceId;
    protected final long kernelTimeStamp;

    public CANMessage(String _gatewayId, String _interfaceId, long kernelTimeStamp) {

        this.gatewayId = Optional.ofNullable(_gatewayId);
        this.interfaceId = Optional.ofNullable(_interfaceId);
        this.kernelTimeStamp = kernelTimeStamp;
        assert gatewayId != null;
        assert interfaceId != null;
        assert kernelTimeStamp > 0;
    }

    /**
     * For messages received by native code, identifies the gateway instance where the message was received.
     * @return i.e. "localhost", may be empty but no null.
     */
    public Optional<String> getGatewayId() {
        return gatewayId;
    }

    /**
     * Identifies the can interface on the gateway node where this message received or must go out from.
     * @return i.e. "can0" may be empty, but not null;
     */
    public Optional<String> getInterfaceId() {
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
