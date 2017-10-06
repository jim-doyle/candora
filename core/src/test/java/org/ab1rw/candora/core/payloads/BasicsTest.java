package org.ab1rw.candora.core.payloads;

import org.ab1rw.candora.core.CANId;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class BasicsTest {

    @Test(expected = IllegalArgumentException.class)
    public void t1() {
        CANId id = new CANId(0x2FFFFFFF);
        Assert.fail("");
    }

    @Test
    public void testValidIDs() {
        CANId id1 = new CANId(0x1FFFFFFF); // largest allowed EFF frame ID
        CANId id2 = new CANId("1FFFFFFF",16);
        Assert.assertEquals(id1, id2);
        System.out.println(id2);
    }

    @Test
    public void test3() {
        byte [] t = new byte [] { 0x3b, 0x0, 0x12 };
        StringBuffer tmp = new StringBuffer();
        tmp.append("0x");
        for (byte b : t) {
            tmp.append(String.format("%02x",b));
            tmp.append(" ");
        }
        System.out.println(tmp.toString());
    }

    @Test
    public void t4() {
        final int [] allowedPayloadWidths = { 0,1,2,3,4,5,6,7,8,12,16,20,24,32,48,64 };
        if (Arrays.binarySearch(allowedPayloadWidths, 20) < 0) {
            throw new IllegalArgumentException("nope");
        }
    }
}
