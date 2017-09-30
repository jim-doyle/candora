package org.ab1rw.candora.core.payloads;


import org.ab1rw.candora.core.CANId;
import org.junit.Assert;
import org.junit.Test;

public class CAN2MessageTest {

    @Test
    public void test1() {
        CANMessage m =  new CAN2Message("gate", "can0", new CANId(1), new byte [] {0x1, 0x2, 0x3}, 0L);
    }

    @Test
    public void test2() {

        double x = 1.1234567890123f;
        double frac = x % 1;
        int intpart = (int)(x - frac);
        int microseconds = (int)(frac * 1E+6);
        Assert.assertTrue(microseconds <= 999999);
        System.out.println(String.format("%d seconds %d microseconds \n", intpart, microseconds));
    }
}
