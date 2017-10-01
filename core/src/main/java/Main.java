import org.ab1rw.candora.core.CANAdapterException;
import org.ab1rw.candora.core.adapter.NativeSocketCANAdapter;

import java.util.logging.LogManager;
import java.util.logging.Logger;

// This is to get oriented with JNI - remove this later once the code matures
public class Main {

    public static void main(String [] argv) {

        Logger logger = Logger.getLogger("Main");
        logger.info("started java logger.");
        NativeSocketCANAdapter nativeSocketCANAdapter = new NativeSocketCANAdapter(true, 0.0);

        try {
            nativeSocketCANAdapter.init();
            logger.info("finished init() method");
            System.out.println("Done with init method.");
        } catch (CANAdapterException e) {
            System.out.println("Received CANAdapterException ");
        }
    }
}
