package org.ab1rw.candora.core;

/**
 * CAN Identifiers are 11 or 29 bit values that identify the source or recipient address of a CANbus message.
 */
public class CANId {
    private final int bits;
    public CANId(int arg) {
    bits=arg;
    }
    public CANId(String arg, int radix) {
        bits=Integer.parseInt(arg, radix);
    }

    public int getBits() {
        return bits;
    }

}
