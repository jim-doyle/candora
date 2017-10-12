package org.ab1rw.candora.core;

import java.io.Serializable;

/**
 * CAN Identifiers are 11 or 29 bit values that identify the source or recipient address of a CANbus message.
 */
public class CANId implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int CAN_EFF_MASK = 0x1FFFFFFF;
    private static final int CAN_SFF_MASK = 0x7FF;

    private final int bits;

    public CANId(int arg) {
       check(arg);
       bits=arg;
    }

    public CANId(String arg, int radix) {
        bits=Integer.parseInt(arg, radix);
        check(bits);
    }

    public int getBits() {
        return bits;
    }

    private static void check(int arg) {
        if (Integer.compareUnsigned(arg,CAN_EFF_MASK)>0) {
            String m = String.format("CAN IDs larger than 0x%02x exceed the range of the Extended Frame Format: 0x%02x", CAN_EFF_MASK, arg);
            throw new IllegalArgumentException(m);
        }
    }

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
