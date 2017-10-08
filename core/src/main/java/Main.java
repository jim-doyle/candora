import org.ab1rw.candora.core.*;
import org.ab1rw.candora.core.adapter.*;
import org.ab1rw.candora.core.payloads.CANErrorMessage;
import org.ab1rw.candora.core.payloads.CANFDMessage;
import org.ab1rw.candora.core.payloads.CANMessage;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;

// This is to get oriented with JNI - remove this later once the code matures
public class Main {

    public static void main(String [] argv) throws CANException {

        Logger logger = Logger.getLogger("Main");
		logger.info("starting demo.");

	    LinuxSocketCANAdapter adapter = new LinuxSocketCANAdapter();
	    adapter.setReceiveTimeout(1.0);

		CANId arduino = new CANId(0x41C);
		CANFDMessage turnLightOnMsg = new CANFDMessage(arduino, new byte [] { 0x1} );
		CANFDMessage turnLightOffMsg = new CANFDMessage(arduino, new byte [] { 0x0} );

		adapter.init();
		int i=0;
		while (true) {
			try {
				CANMessage m = adapter.receive();
				if (m instanceof CANErrorMessage) {
					logger.log(Level.INFO,"received error payload: {0}", m.toString());
				}
				if (m instanceof CANFDMessage) {
					logger.log(Level.INFO,"received payload: {0}", m.toString());
				}

				// alternate on and off lights on arduino
				if (i++ % 2==0) adapter.send(turnLightOnMsg);
				else adapter.send(turnLightOffMsg);

			} catch (CANReceiveTimeoutException timeout) {
				logger.info("receive timeout...");
			}

		}
    }
}
