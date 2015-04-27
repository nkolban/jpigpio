package jpigpio;

public class Utils {
	public static void addShutdown(JPigpio pigpio) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				pigpio.gpioTerminate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
	}
} // End of class
// End of file