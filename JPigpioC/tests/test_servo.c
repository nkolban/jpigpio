#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <pigpio.h>
#include <errno.h>


int main(void) {
	int rc = gpioInitialise();
	if (rc < 0) {
		printf("gpioInitialise() failed: %d\n", rc);
		exit(-1);
	}
	int pwmPin = 17;
	int servoSweepDuration = 500; // Time in ms

	printf("pwmPin pin = %d\n", pwmPin);
	printf("servoSweepDuration = %d seconds\n", servoSweepDuration);

	int direction = 0; // 0 = up, 1 = down
	while(1) {
		unsigned int startTime = gpioTick();
		unsigned int sweepTime = servoSweepDuration * 1000 / 2;
		unsigned int delta = gpioTick() - startTime;
		while(delta < sweepTime) {
			// Position is going to be between 0 and 1 and represents how far along the sweep
			// we are.
			double position = (double)delta/sweepTime;

			unsigned int timeToWriteHighUSecs;
			position = -0.4;
			if (direction == 0) {
				timeToWriteHighUSecs = (position + 1.0) * 1000;
			} else {
				timeToWriteHighUSecs = (2.0 - position) * 1000;
			}

			printf("Setting pulse width %d, %d, %d\n", timeToWriteHighUSecs, delta, startTime);
			gpioServo(pwmPin, timeToWriteHighUSecs);
			gpioDelay(20 * 1000);

			delta = gpioTick() - startTime;
		} // End of a sweep in one direction

		// Switch direction
		if (direction == 0) {
			direction = 1;
		} else {
			direction = 0;
		}
	} // End of while true
	return 0;
}
