package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;

public class Test_Timings {
	public static void main(String args[]) {
		System.out.println("Test_Timings");
		Test_Timings test_Timings = new Test_Timings();
		test_Timings.run();
	}

	public void run() {

		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			long last = pigpio.gpioTick();
			while (true) {
				long tick = pigpio.gpioTick();
				System.out.println(String.format("Tick: %d, delta: %d", tick, tick-last));
				last = tick;
			}
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
} // End of class
// End of file