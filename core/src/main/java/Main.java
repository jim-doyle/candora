import org.ab1rw.candora.core.*;
import org.ab1rw.candora.core.adapter.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.*;

// This is to get oriented with JNI - remove this later once the code matures
public class Main {

    public static void main(String [] argv) throws Exception {

        Logger logger = Logger.getLogger("Main");
        logger.info("started java logger.");
	
	    System.loadLibrary("candora-native");

  NativeSocketCANAdapter nativeSocketCANAdapter = new NativeSocketCANAdapter(true, 60.0);

            nativeSocketCANAdapter.init();
	    NativeCANFrame f = new NativeCANFrame();
	    nativeSocketCANAdapter.receive(f);
	    System.out.println("Got "+f.can_dlc+" bytes.");
	    for (int i=0; i<f.can_dlc; i++) {
	       System.out.print(String.format("%x,",f.can_data[i]));
	    }
	    System.out.println();
	    System.out.println(f.can_data.length);
	    System.out.println();
            System.out.println("Done with init method.");
    }
}
