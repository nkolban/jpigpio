package tests;

import jpigpio.GPIO;
import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.devices.TM1638;

public class Test_TM1638 {
	private int stbPin = 5;
	private int clkPin = 6;
	private int dioPin = 13;
	private GPIO stbGpio, clkGpio, dioGpio;
	public static void main(String args[]) {
		System.out.println("Test_TM1638");
		Test_TM1638 app = new Test_TM1638();
		app.run();
	}

	public void run() {

		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			stbGpio = new GPIO(pigpio, stbPin, JPigpio.PI_OUTPUT);
			clkGpio = new GPIO(pigpio, clkPin, JPigpio.PI_OUTPUT);
			dioGpio = new GPIO(pigpio, dioPin, JPigpio.PI_OUTPUT);
			
			TM1638 tm1638 = new TM1638(pigpio, stbGpio, clkGpio, dioGpio);
			tm1638.writeNumber(0);
			
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
} // End of class
// End of file