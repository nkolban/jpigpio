package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.sensors.WiiNunchuck;

public class Test_Nunchuck2 {

	public static void main(String[] args) {
		Test_Nunchuck2 app = new Test_Nunchuck2();
		app.run();
	}

	public void run() {
		try {
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			WiiNunchuck wiiNunchuck = new WiiNunchuck(pigpio);
			wiiNunchuck.initialize();

			while (true) {
				wiiNunchuck.readValue();
				System.out.println("Nunchuck: " + wiiNunchuck.toString());
			} // End of while true
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	} // End of run
} // End of Test_Nunchuck2
// End of file