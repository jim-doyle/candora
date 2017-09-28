package org.ab1rw.candora.core.payloads;

import org.ab1rw.candora.core.CANId;
import org.junit.Test;

public class CANErrorMessageTest {

    @Test
    public void test1() {
        CANErrorMessage m = new CANErrorMessage("gate", "can0", new CANId(1), new byte [] {0x1, 0x2, 0x3}, 0L);
        // XXX TO DO - add validation tests!
    }
}
