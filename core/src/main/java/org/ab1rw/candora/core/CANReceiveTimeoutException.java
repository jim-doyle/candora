package org.ab1rw.candora.core;
/**
 * If a CAN packet is not received without the adapter's receive timeout window, then
 * this exception will be generated.
 */
public class CANReceiveTimeoutException extends CANException {

    private static final long serialVersionUID = 1L;

    public CANReceiveTimeoutException() {
        super("syscall recvfrom() return EAGAIN following socket receive timeout. ");
    }
    public CANReceiveTimeoutException(String arg) {
	super(arg);
    }
}
