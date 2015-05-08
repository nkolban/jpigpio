package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;

public class Test_Nunchuck {

	public static void main(String[] args) {
		Test_Nunchuck app = new Test_Nunchuck();
		app.run();
	}

	public void run() {
		try {
			int NUNCHUCK_DEVICE = 0x52;
			int PI_I2CBUS = 1;
			// IPigpio pigpio = new Pigpio();
			//JPigpio pigpio = new PigpioSocket("localhost", 8888);
			JPigpio pigpio = new Pigpio();

			System.out.println("Testing the nunchuck through I2C");
			pigpio.gpioInitialize();
			int handle = pigpio.i2cOpen(PI_I2CBUS, NUNCHUCK_DEVICE);
			if (handle < 0) {
				System.out.println("Error setting up I2C: " + handle);
				System.exit(0);
			}

			byte data[] = { 0x40, 0x00 };
			pigpio.i2cWriteDevice(handle, data);
			pigpio.gpioDelay(500);

			while (true) {
				byte data2[] = { 0x00 };
				pigpio.i2cWriteDevice(handle, data2);
				pigpio.gpioDelay(200);
				byte data3[] = new byte[6];
				pigpio.i2cReadDevice(handle, data3);

				byte bytes[] = data3;
				// (d) 1 2 4 8 16 32 64 128
				// (h) 1 2 4 8 10 20 40 80
				int joyX = bytes[0];
				int joyY = bytes[1];
				int accelX = (bytes[2] << 2) | ((bytes[5] & 0xc0) >> 6);
				int accelY = (bytes[3] << 2) | ((bytes[5] & 0x30) >> 4);
				int accelZ = (bytes[4] << 2) | ((bytes[5] & 0x0c) >> 2);
				int c = (bytes[5] & 0x02) >> 1;
				int z = bytes[5] & 0x01;

				// printf("data: %x %x %x %x %x %x\n", bytes[0], bytes[1],
				// bytes[2],
				// bytes[3], bytes[4], bytes[5]);
				System.out.println(String.format("data: joyX=%x joyY=%x accelX=%x accelY=%x accelZ=%x c=%x z=%x", joyX, joyY, accelX, accelY, accelZ, c, z));
			} // End of while true
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	} // End of run
}
