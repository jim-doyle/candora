package org.ab1rw.candora.core.payloads;
import org.ab1rw.candora.core.CANId;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An Error Message from the Linux SocketCAN facility.
 *
 * see file:/usr/include/linux/can/error.h'  Many of the bitmasks and code values, as well as the
 * encoding convention in the payload field are covered in this C header file.
 *
 */
public class CANErrorMessage extends CANMessage {

    private final List<String> messages;
    private ERROR_CATEGORY errorCategory;
    protected final byte [] payload;

    /**
     * Error Categories reported by the Linux CANSocket stack.  For each category, additional
     * message meta-data may able be available in the payload fields. These additional details
     * are decoded into message strings.
     */
    public static enum ERROR_CATEGORY {
        TX_Timeout(0x1, "TX timeout (by netdevice driver)"),
        Lost_Arbitration(0x2,"Lost Arbitration"),
        Controller_Fault(0x4, "Controller Fault"),
        Protocol_Violation(0x8, "Protocol Violations"),
        Transceiver_Fault(0x10, "Transceiver Fault"),
        No_ACK_On_Transmit(0x20, "No ACK on Transmit"),
        Bus_Off(0x40, "Bus off"),
        Bus_Error(0x80, "Bus error (may flood)"),
        Bus_Restart(0x100, "Controller restarted");

        private final short mask;
        private final String description;

        private ERROR_CATEGORY(int m, String d) {
            mask= (short)m;
            description=d;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "ERROR_CATEGORY{" +
                    "description='" + description + '\'' +
                    '}';
        }
    }

    public CANErrorMessage(String gatewayId, String interfaceId, int hdr, byte[] _payload, long kernelTimeStamp) {
        super(gatewayId, interfaceId, kernelTimeStamp);
        payload=_payload;

        for (ERROR_CATEGORY e : ERROR_CATEGORY.values()) {
            if (e.mask == hdr) {
                errorCategory = e;
                break;
            }
        }
        if (errorCategory == null) {
            throw new IllegalArgumentException("XXX TODO ");
        }
        // decode the details of error category and attach to this instance.
        messages = Collections.unmodifiableList(decode());
    }

    public ERROR_CATEGORY getErrorCategory() {
        return errorCategory;
    }

    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "CANErrorMessage{" +
                "messages=" + messages +
                ", errorCategory=" + errorCategory +
                '}';
    }




    /**
     * Provides a decode of SocketCAN mystery bits into human readable messages.
     * Depending on the Error Category, further details are hidden in either bits,
     * or as byte values in the message payload area.
     */
    private List<String> decode() {
        List<String> messages = new LinkedList<String>();

        // Lost_Arbitration, the bit where the fault happened is in data[0]
        if (errorCategory.equals(ERROR_CATEGORY.Lost_Arbitration)) {
            messages.add(String.format("Arbitration failure observed at bit position %d", (int) payload[0]));
        }

        //  Controller_Fault, details in data[1]
        if (errorCategory.equals(ERROR_CATEGORY.Controller_Fault)) {
            if (payload[1] == 0) messages.add("Unspecified error reported in payload[1]");
            if ((payload[1] & 0x1) != 0) messages.add("Receive buffer overflow");
            if ((payload[1] & 0x2) != 0) messages.add("Transmit buffer overflow");
            if ((payload[1] & 0x4) != 0) messages.add("Reached warning level for RX errors");
            if ((payload[1] & 0x8) != 0) messages.add("Reached warning level for TX errors");
            if ((payload[1] & 0x10) != 0) messages.add("Reached error passive status RX");
            if ((payload[1] & 0x20) != 0)
                messages.add("Reached error passive status TX, at least one error counter exceeds the protocol-defined level of 127.  ");
        }

        // Protocol_Violation details in data[2] and data[3]
        if (errorCategory.equals(ERROR_CATEGORY.Protocol_Violation)) {
            if (payload[2] == 0) messages.add("Unspecified error reported in data[2]");
            if ((payload[2] & 0x1) != 0) messages.add("Single bit error");
            if ((payload[2] & 0x2) != 0) messages.add("Frame format error");
            if ((payload[2] & 0x4) != 0) messages.add("Bit stuffing error");
            if ((payload[2] & 0x8) != 0) messages.add("Unable to send dominant bit");
            if ((payload[2] & 0x10) != 0) messages.add("Unable to send recessive bit");
            if ((payload[2] & 0x20) != 0) messages.add("Bus Overload");
            if ((payload[2] & 0x40) != 0) messages.add("Active Error Announcement");
            if ((payload[2] & 0x80) != 0) messages.add("Error occured on Transmission");


            switch (payload[3]) {
                case 0:
                    messages.add("Unspecified error reported in payload[3]");
                    break;
                case 0x3:
                    messages.add("Start of Frame");
                    break;
                case 0x2:
                    messages.add("ID bits 28 - 21 (SFF: 10 - 3)");
                    break;
                case 0x6:
                    messages.add("ID bits 20 - 18 (SFF: 2 - 0 )");
                    break;
                case 0x4:
                    messages.add("substitute RTR (SFF: RTR)");
                    break;
                case 0x5:
                    messages.add("identifier extension");
                    break;
                case 0x7:
                    messages.add("ID bits 17-13");
                    break;
                case 0xF:
                    messages.add("ID bits 12-5");
                    break;
                case 0xE:
                    messages.add("ID bits 4-0");
                    break;
                case 0xC:
                    messages.add("RTR");
                    break;
                case 0xD:
                    messages.add("reserved bit 1");
                    break;
                case 0x9:
                    messages.add("reserved bit 0");
                    break;
                case 0xB:
                    messages.add("data length code");
                    break;
                case 0xA:
                    messages.add("data section");
                    break;
                case 0x8:
                    messages.add("CRC sequence");
                    break;
                case 0x18:
                    messages.add("CRC delimiter");
                    break;
                case 0x19:
                    messages.add("ACK slot");
                    break;
                case 0x1B:
                    messages.add("ACK delimiter");
                    break;
                case 0x1A:
                    messages.add("end of frame");
                    break;
                case 0x12:
                    messages.add("intermission");
                    break;
                default:
                    messages.add("Invalid code XX for data[3]");
            }

        }

        // Transceiver_Fault detail in data[4]
        if (errorCategory.equals(ERROR_CATEGORY.Transceiver_Fault)) {
            switch (payload[4]) {
                case 0:
                    messages.add("Unspecified error reported in payload[4]");
                    break;
                case 0x4:
                    messages.add("CANH Short to BAT");
                    break;
                case 0x5:
                    messages.add("CANH Short to VCC");
                    break;
                case 0x6:
                    messages.add("CANH Short to GND");
                    break;
                case 0x7:
                    messages.add("No Wire");
                    break;
                case 0x40:
                    messages.add("CANL Short to BAT");
                    break;
                case 0x60:
                    messages.add("CANL Short to VCC");
                    break;
                case 0x70:
                    messages.add("CANL Short to GND");
                    break;
                case (byte) 0x80:
                    messages.add("CANL Short to CANH");
                    break;
                default:
                    messages.add("Invalid code XX for data[4]");

            }
        }
        return messages;
    }

}
