package org.ab1rw.candora.core.payloads;


import org.ab1rw.candora.core.CANId;
import org.junit.Test;

public class CAN2MessageTest {

    @Test
    public void test1() {
        CANMessage m =  new CAN2Message("gate", "can0", new CANId(1), new byte [] {0x1, 0x2, 0x3}, 0L);
    }

}
