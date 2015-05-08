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
		Test_ServoSweep app = new Test_ServoSweep();
		app.run();
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