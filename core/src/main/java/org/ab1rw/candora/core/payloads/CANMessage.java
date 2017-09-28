package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.util.Arrays;

/**
 * A generic CANbus message payload obtained from the Linux SocketCAN facility.
 * Note that this is an immutable object.
 */
public abstract class CANMessage implements java.io.Serializable {

    protected final String gatewayId;
    protected final String interfaceId;
    protected final CANId id;
    protected final byte [] payload;
    protected final long kernelTimeStamp;

    public CANMessage(String gatewayId, String interfaceId, CANId id, byte[] payload, long kernelTimeStamp) {
        this.gatewayId = gatewayId;
        this.interfaceId = interfaceId;
        this.id = id;
        this.payload = payload;
        this.kernelTimeStamp = kernelTimeStamp;
        // XXX todo add assertions
        assert id != null;
        assert payload != null;
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

    public CANId getId() {
        return id;
    }

    public int getPayloadLength() {
        return payload.length;
    }

    public byte[] getPayload() {
        // important, return a copy - not a ref of the instance, otherwise we create a mutability exposure.
        return Arrays.copyOf(payload, payload.length);
    }

    public long getKernelTimeStamp() {
        return kernelTimeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CANMessage that = (CANMessage) o;

        if (kernelTimeStamp != that.kernelTimeStamp) return false;
        if (gatewayId != null ? !gatewayId.equals(that.gatewayId) : that.gatewayId != null) return false;
        if (interfaceId != null ? !interfaceId.equals(that.interfaceId) : that.interfaceId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return Arrays.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        int result = gatewayId != null ? gatewayId.hashCode() : 0;
        result = 31 * result + (interfaceId != null ? interfaceId.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(payload);
        result = 31 * result + (int) (kernelTimeStamp ^ (kernelTimeStamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        // XXX todo, print the payload as a hexstring please...
        return "CANMessage{" +
                "gatewayId='" + gatewayId + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", id=" + id +
                ", payload=" + Arrays.toString(payload) +
                ", kernelTimeStamp=" + kernelTimeStamp +
                '}';
    }
}
