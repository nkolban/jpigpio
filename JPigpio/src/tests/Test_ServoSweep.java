package tests;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.PigpioSocket;

public class Test_ServoSweep {
	private final int servoPin = 17;
	private final int min = 500;
	private final int max = 2500;

	public static void main(String args[]) {
		System.out.println("Starting Test_ServoSweep");
		Test_ServoSweep test_servoSweep = new Test_ServoSweep();
		test_servoSweep.run();
	}

	public void run() {

		try {
			JPigpio pigpio = new PigpioSocket("localhost", 8888);
			int servoSweepDuration = 500; // Time in ms
			int direction = 0; // 0 = up, 1 = down
			while (true) {
				long startTime = pigpio.gpioTick();
				int sweepTime = servoSweepDuration * 1000 / 2;
				long delta = pigpio.gpioTick() - startTime;
				while (delta < sweepTime) {
					// Position is going to be between 0 and 1 and represents
					// how far along the sweep we are.
					double position = (double) delta / sweepTime * (max - min);

					int timeToWriteHighUSecs;
					if (direction == 0) {
						timeToWriteHighUSecs = (int) (min + position);
					} else {
						timeToWriteHighUSecs = (int) (max - position);
					}

					System.out.println(String.format("Setting pulse width %d, delta=%d, startTime=%d\n", timeToWriteHighUSecs, delta, startTime));
					pigpio.gpioServo(servoPin, timeToWriteHighUSecs);
					System.out.println("A");
					pigpio.gpioDelay(30);
					System.out.println("B");

					long x = pigpio.gpioTick();
					System.out.println("x = " + x);
					delta = pigpio.gpioTick() - startTime;
				} // End of a sweep in one direction

				// We have ended a sweep, let us now go in the other direction!
				System.out.println("End sweep");
				// Switch direction
				if (direction == 0) {
					direction = 1;
				} else {
					direction = 0;
				}
			} // End of while true

		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
}
/*
 * 
 * int main(void) { int rc = gpioInitialise(); if (rc < 0) {
 * printf("gpioInitialise() failed: %d\n", rc); exit(-1); } int pwmPin = 17; int
 * servoSweepDuration = 500; // Time in ms
 * 
 * printf("pwmPin pin = %d\n", pwmPin);
 * printf("servoSweepDuration = %d seconds\n", servoSweepDuration);
 * 
 * int direction = 0; // 0 = up, 1 = down while(1) { unsigned int startTime =
 * gpioTick(); unsigned int sweepTime = servoSweepDuration * 1000 / 2; unsigned
 * int delta = gpioTick() - startTime; while(delta < sweepTime) { // Position is
 * going to be between 0 and 1 and represents how far along the sweep // we are.
 * double position = (double)delta/sweepTime;
 * 
 * unsigned int timeToWriteHighUSecs; position = -0.4; if (direction == 0) {
 * timeToWriteHighUSecs = (position + 1.0) * 1000; } else { timeToWriteHighUSecs
 * = (2.0 - position) * 1000; }
 * 
 * printf("Setting pulse width %d, %d, %d\n", timeToWriteHighUSecs, delta,
 * startTime); gpioServo(pwmPin, timeToWriteHighUSecs); gpioDelay(20 * 1000);
 * 
 * delta = gpioTick() - startTime; } // End of a sweep in one direction
 * 
 * // Switch direction if (direction == 0) { direction = 1; } else { direction =
 * 0; } } // End of while true return 0; }
 */