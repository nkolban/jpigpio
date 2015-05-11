package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.sensors.HC_SR04;

public class Test_HC_SR04 {

	private int triggerPin = 17;
	private int echoPin = 18;

	public static void main(String args[]) {
		System.out.println("Test HC-SR04");
		Test_HC_SR04 app = new Test_HC_SR04();
		app.run();
	}

	public void run() {
		try {
			// JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.setDebug(false);
			pigpio.gpioInitialize();

			
			// Create an instance of the HC_SR04 ultrasonic device
			HC_SR04 hcSR04 = new HC_SR04(pigpio, triggerPin, echoPin);
			Utils.addShutdown(pigpio);
			
			// Set the mode of the pins we will be using.
			pigpio.gpioSetMode(triggerPin, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(echoPin, JPigpio.PI_INPUT);
			while (true) {
				System.out.println("Distance: " + hcSR04.getMetricDistance() * 100 + "cm");
				try {
					Thread.sleep(250);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
} // End of class
// End of file