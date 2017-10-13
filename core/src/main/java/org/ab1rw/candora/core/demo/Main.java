package org.ab1rw.candora.core.demo;

import org.ab1rw.candora.core.*;
import org.ab1rw.candora.core.payloads.CANId;
import org.ab1rw.candora.core.adapter.*;
import org.ab1rw.candora.core.payloads.CANErrorMessage;
import org.ab1rw.candora.core.payloads.CANFDMessage;
import org.ab1rw.candora.core.payloads.CANMessage;


import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

/**
 * This is a demo program for Candora running on Raspberry Pi interacting with an Arduino over canbus.
 * This demo receives traffic promiscuously and prints to stdout, it will also send a canbus message
 * to the arduino that results in an LED being turned alternately on and off.  The purpose here it
 * demonstrate the programming model and correctness of the code.
 */
public class Main {

    public static void main(String[] argv) throws CANException {

        final CANId arduino = new CANId(0x41C);

        Logger logger = Logger.getLogger("org.ab1rw.candora.core.demo.Main");
        logger.info("starting demo with Arduino.");

        LinuxSocketCANAdapter adapter = new LinuxSocketCANAdapter("localhost", "can0");
        adapter.setReceiveTimeout(1.0);  // 1 second receive timeout.
        adapter.init();
        final CANFDMessage turnLightOnMsg = adapter.create(arduino, new byte[]{0x1}, (byte) 0);
        final CANFDMessage turnLightOffMsg = adapter.create(arduino, new byte[]{0x0}, (byte) 0);

        int i = 0;
        while (true) {
            try {
                CANMessage m = adapter.receive();
                if (m instanceof CANErrorMessage) {
                    logger.log(Level.INFO, "received error payload: {0}", m.toString());
                }
                if (m instanceof CANFDMessage) {
                    logger.log(Level.INFO, "received payload: {0}", m.toString());
                }

                // alternate on and off lights on arduino
                if (i++ % 2 == 0) adapter.send(turnLightOnMsg);
                else adapter.send(turnLightOffMsg);

            } catch (CANReceiveTimeoutException timeout) {
                logger.info("receive timeout.");
            }

        }
    }
}
