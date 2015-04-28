package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

public class Test_Template {
	public static void main(String args[]) {
		System.out.println("Test_AppName");
		Test_Template app = new Test_Template();
		app.run();
	}

	public void run() {

		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
} // End of class
// End of file