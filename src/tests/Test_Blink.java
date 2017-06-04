package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

/**
 * Set a pin high and then low forever.  The pin is commonly connected to an LED to cause it to
 * blink on and off.
 * 
 * author Neil Kolban
 * date 2015-04-26
 *
 */
public class Test_Blink {
	/**
	 * This is the gpio pin to set output and pulse on and off.
	 */
	private final int BLINK_PIN = 17;
	
	
	/**
	 * Bootstrap the application.
	 * @param args Standard application arguments (not used).
	 */
	public static void main(String args[]) {
		System.out.println("Test_Blink");
		Test_Blink app = new Test_Blink();
		app.run();
	}

	/**
	 * Define a run function to perform the work in the context of an
	 * instance of the class.
	 */
	public void run() {
		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			
			// Initialize our library
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			
			// Set the gpio pin to its output mode
			pigpio.gpioSetMode(BLINK_PIN, JPigpio.PI_OUTPUT);
			
			// Loop forever
			while (true) {
				// Set the pin high to light the LED
				pigpio.gpioWrite(BLINK_PIN, JPigpio.PI_HIGH);
				pigpio.gpioDelay(500 * 1000);
				
				// Set the pin low to darken the LED
				pigpio.gpioWrite(BLINK_PIN, JPigpio.PI_LOW);
				pigpio.gpioDelay(500 * 1000);
			}
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	} // End of run
} // End of class
// End of file