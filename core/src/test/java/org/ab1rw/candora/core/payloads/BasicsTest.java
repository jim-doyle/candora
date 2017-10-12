package org.ab1rw.candora.core.payloads;

import org.ab1rw.candora.core.CANId;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Tests the basic compliance of CAN objects represented in Java, largely allowed ranges and limits.
 */
public class BasicsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testCANIdlimits() {
        CANId id = new CANId(0x2FFFFFFF);
        Assert.fail("CAN ID 0x2FFFFFFF exceeds the limit allowed 29 bit EFF.");
    }

    @Test
    public void testCANIdctor() {
        CANId id1 = new CANId(0x1FFFFFFF); // largest allowed EFF frame ID
        CANId id2 = new CANId("1FFFFFFF",16);
        Assert.assertEquals("Ctor should take integer and string args and behave same way.",id1, id2);
    }

    @Test
    public void testPayloadPrintConvention() {
        byte [] t = new byte [] { 0x3b, 0x0, 0x12, 0x1 };
        StringBuffer tmp = new StringBuffer();
        tmp.append("0x");
        for (byte b : t) {
            tmp.append(String.format("%02x",b));
            tmp.append(" ");
        }
        tmp.deleteCharAt(tmp.length()-1);
        System.out.println(tmp.toString());
        Assert.assertEquals("Payload should print as 0x3b 00 12 01","0x3b 00 12 01", tmp.toString());
    }

    @Test
    public void testCANFDPayloadLimits() {

        //CANId id1 = new CANId(0x1FFFFFFF);
        //CANFDMessage m = new CANFDMessage(id1, new byte [21] );

        int trial = 64;
        int [] allowedPayloadWidths = { 0,1,2,3,4,5,6,7,8,12,16,20,24,32,48,64 };
        int i=0;
        int use=0;
        while (i<allowedPayloadWidths.length) {
            if (allowedPayloadWidths[i] == trial) {
                use=i;
                break;
            }
            if (allowedPayloadWidths[i] < trial && allowedPayloadWidths[i+1] > trial) {
                use = i + 1;
                break;
            }
            else i++;
        }
        System.out.println(trial+" use "+use+" for "+allowedPayloadWidths[use]);
    }
}
