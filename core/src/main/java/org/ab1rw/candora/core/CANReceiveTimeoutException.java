package org.ab1rw.candora.core;

/**
 * If a CAN packet is not received without the adapter's receive timeout window, then
 * this exception will be generated.
 */
public class CANReceiveTimeoutException extends CANException {

    public CANReceiveTimeoutException(double timeout) {
        super("");
    }
}
