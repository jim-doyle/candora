package org.ab1rw.candora.core.payloads;

import java.io.Serializable;

/**
 * CAN Identifiers are 11 or 29 bit values that identify the source or recipient address of a CANbus message.
 */
public class CANId implements Serializable {

    private static final long serialVersionUID = -4772986837860063915L;

    private static final int CAN_EFF_MASK = 0x1FFFFFFF;
    private static final int CAN_SFF_MASK = 0x7FF;

    private final int bits;

    /**
     * Create CAN identifier to prepare a message for transmission.
     * <pre>
     *     Example:
     *          CANId myarduino = new CANId(0x7FE);
     *          CANId thermometer2 = new CANId(73414);
     * </pre>
     * @param arg i.e. 454 (decimal) or 0x7FE (
     */
    public CANId(int arg) {
       check(arg);
       bits=arg;
    }

    /**
     * Overloaded ctor ; Convenience - Create a CANId from a String value.
     * <pre>
     *     Example:
     *          CANId myarduino = new CANId("7FE", 16);
     *          CANId thermometer2 = new CANId("73414",10);
     *          CANId thermometer1 = new CANId("01001010111101", 2);
     * </pre>
     * @param arg value, i.e. 7fe, 01011101, 93411
     * @param radix base, i.e. 10 for decimal, 16 for hex, 2 for binary.
     */
    public CANId(String arg, int radix) {
        bits=Integer.parseInt(arg, radix);
        check(bits);
    }

    /**
     * Returns the CANId value.
     * @return CAN ID value, no larger than 0x1FFFFFFF, the limit for Extended Frame addresses.
     */
    public int getBits() {
        return bits;
    }

    /**
     * Test to see if this ID is not an SFF frame.
     * @return true if the ID exceeds the limit for an SFF frame
     */
    public boolean isExtendedFrame() { return bits > CAN_SFF_MASK; }

    /** Bounds check */
    private static void check(int arg) {
        if (Integer.compareUnsigned(arg,CAN_EFF_MASK)>0) {
            String m = String.format("CAN IDs larger than 0x%02x exceed the range of the Extended Frame Format: 0x%02x", CAN_EFF_MASK, arg);
            throw new IllegalArgumentException(m);
        }
    }

    /**
     * Convenience to String. CAN Ids are either printed as 3 hex fields, or 8 hex fields. This
     * is done for readability in logs and output.  Example:
     *
     * @return i.e. CANId{id=12b4e608}
     */
    @Override
    public String toString() {
        return toStringHelper(bits);
    }

    private static String toStringHelper(int arg) {
       if (arg <= CAN_EFF_MASK) {
           return String.format("CANId{id=%08x}",arg);
       } else {
           // SFF address format
           return String.format("CANId{id=%03x}",arg);
       }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CANId canId = (CANId) o;
        return bits == canId.bits;
    }

    @Override
    public int hashCode() {
        return bits;
    }
}
