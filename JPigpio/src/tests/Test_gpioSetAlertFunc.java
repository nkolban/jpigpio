package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

/**
 * Test the gpioSetAlertFunc capability.
 * @author Neil Kolban
 *
 */
public class Test_gpioSetAlertFunc {

	/**
	 * The pin to watch for changes in state.
	 */
	private final int TESTPIN = 18;

	public static void main(String args[]) {
		System.out.println("Test_gpioSetAlertFunc");
		Test_gpioSetAlertFunc app = new Test_gpioSetAlertFunc();
		app.run();
	}

	/**
	 * Main entry point to the test in the context of an object instance.
	 */
	public void run() {
		try {
			// Initialize the environment
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			
			// Set the test pin to input
			pigpio.gpioSetMode(TESTPIN, JPigpio.PI_INPUT);
			
			// Define a callback function
			pigpio.gpioSetAlertFunc(TESTPIN, (gpio, level, tick) -> {
				System.out.println(String.format("NotificationListener in Java: We received an alert on: %d with %d at %d", gpio, level, tick));
			});
			
			System.out.println(String.format("Watching for changes on pin: %d", TESTPIN));
			
			// Sleep for a minute
			Thread.sleep(60 * 1000);
			
			// Cleanup
			pigpio.gpioTerminate();
			
		} catch (PigpioException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	} // End of run
} // End of class
// End of file