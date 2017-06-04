package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.devices.Stepper;

public class Test_Stepper {
	
	private int blueGpio=JPigpio.PI_GPIO5; // IN1
	private int pinkGpio=JPigpio.PI_GPIO6; // IN2
	private int yellowGpio=JPigpio.PI_GPIO13; // IN3
	private int orangeGpio=JPigpio.PI_GPIO19; // IN4
	private int delay = 2000; // uSecs
	
	public static void main(String args[]) {
		System.out.println("Test_Stepper");
		Test_Stepper app = new Test_Stepper();
		app.run();
	}

	public void run() {
		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			pigpio.gpioSetMode(blueGpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(pinkGpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(yellowGpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(orangeGpio, JPigpio.PI_OUTPUT);
			Stepper stepper = new Stepper(pigpio, blueGpio, pinkGpio, yellowGpio, orangeGpio);
			System.out.println("About to start stepping ...");
			while(true) {
				stepper.forward();
				pigpio.gpioDelay(delay);
			}
			
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	} // End of run
} // End of class
// End of file