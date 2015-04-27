package tests;

import jpigpio.Alert;
import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

public class Test_gpioSetAlertFunc {

	private final int TESTPIN = 18;

	public static void main(String args[]) {
		System.out.println("Test_gpioSetAlertFunc");
		Test_gpioSetAlertFunc test_gpioSetAlertFunc = new Test_gpioSetAlertFunc();
		test_gpioSetAlertFunc.run();
	}

	public void run() {

		try {
			// JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			
			// Set the test pin to input
			pigpio.gpioSetMode(TESTPIN, JPigpio.PI_INPUT);
			
			// Define a callback function
			Alert alert = new Alert() {
				@Override
				public void alert(int pin, int level, long tick) {
					System.out.println(String.format("Callback in Java: We received an alert on: %d with %d at %d", pin, level, tick));
				}
			};
			
			pigpio.gpioSetAlertFunc(TESTPIN, alert);
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
	}
} // End of class
// End of file