package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.util.Arrays;

/**
 * CAN Message base class.
 * Note that this is an immutable object.
 */
public abstract class CANMessage implements java.io.Serializable {

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
     *
     * @return i.e. "localhost"
     */
    public String getGatewayId() {
        return gatewayId;
    }

    /**
     *
     * @return i.e. "can0"
     */
    public String getInterfaceId() {
        return interfaceId;
    }

}
