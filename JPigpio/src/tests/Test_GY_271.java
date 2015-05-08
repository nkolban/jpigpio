package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.sensors.GY_271;

public class Test_GY_271 {

	public static void main(String[] args) {
		Test_GY_271 app = new Test_GY_271();
		app.run();
	}

	public void run() {
		try {
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();

			GY_271 gy_271 = new GY_271(pigpio);
			gy_271.initialize();
			pigpio.gpioDelay(1000);
			gy_271.setGain(GY_271.GAIN_1090);
			pigpio.gpioDelay(1000);
			System.out.println("Device id: " + gy_271.getId());
			while (true) {
				//System.out.println("Status: " + gy_271.getStatus());
				gy_271.readValue();
				System.out.println("Values: " + gy_271.toString());
//				while((gy_271.getStatus() & 0b10) != 0) {
//					System.out.println("Locked");
//					pigpio.gpioDelay(100, JPigpio.PI_MICROSECONDS);
//				}
				pigpio.gpioDelay(500, JPigpio.PI_MILLISECONDS);
			} // End of while true
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	} // End of run
} // End of Test_Nunchuck2
// End of file