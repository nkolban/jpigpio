/*
 * ultrasonic.c
 * Test the getUltrasonicDistance function.
 *
 *  Created on: Apr 7, 2015
 *      Author: kolban
 */
#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <pigpio.h>
#include <errno.h>


int main(void) {
	int NUNCHUCK_DEVICE = 0x52;
	int PI_I2CBUS = 1;

	printf("Testing the nunchuck through I2C\n");
	int rc = gpioInitialise();
	printf("rc from gpioInitialise: %d\n", rc);
	int handle = i2cOpen(PI_I2CBUS, NUNCHUCK_DEVICE, 0);
	if (handle) {
		printf("Error setting up I2C: %d\n", errno);
		exit(0);
	}
	char buf[2] = {0x40, 0x00};
	i2cWriteDevice(handle, buf, sizeof(buf));
	gpioDelay(500);

	while(1) {
		char buf[1] = {0x00};
		i2cWriteDevice(handle, buf, sizeof(buf));
		gpioDelay(500);
		char bufResponse[6];
		i2cReadDevice(handle, bufResponse, sizeof(bufResponse));

		char *bytes = bufResponse;
		// (d) 1 2 4 8 16 32 64 128
		// (h) 1 2 4 8 10 20 40  80
		int joyX = bytes[0];
		int joyY = bytes[1];
		int accelX = (bytes[2] << 2) | ((bytes[5] & 0xc0) >> 6);
		int accelY = (bytes[3] << 2) | ((bytes[5] & 0x30) >> 4);
		int accelZ = (bytes[4] << 2) | ((bytes[5] & 0x0c) >> 2);
		int c = (bytes[5] & 0x02) >> 1;
		int z = bytes[5] & 0x01;

		//printf("data: %x %x %x %x %x %x\n", bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5]);
		printf("data: joyX=%x joyY=%x accelX=%x accelY=%x accelZ=%x c=%x z=%x\n", joyX, joyY, accelX, accelY, accelZ, c, z);
	}
	return 0;
}
