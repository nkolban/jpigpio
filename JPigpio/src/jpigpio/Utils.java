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
	
	public static int mapToInt(int value, int sourceLow, int sourceHigh, int targetLow, int targetHigh) {
		double pos = (value-sourceLow)/(double)(sourceHigh - sourceLow);
		System.out.println("Pos: " + pos);
		System.out.println("tHigh - tLow = " + (targetHigh - targetLow));
		System.out.println("r = " + (targetLow + ((targetHigh - targetLow) *  pos)));
		return (int)(targetLow + ((targetHigh - targetLow) *  pos));
	}
} // End of class
// End of file