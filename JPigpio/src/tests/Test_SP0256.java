package tests;

import jpigpio.GPIO;
import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.devices.SP0256;

public class Test_SP0256 {
	private GPIO aldGpio;
	private GPIO lrqGpio;
	private GPIO a1Gpio;
	private GPIO a2Gpio;
	private GPIO a3Gpio;
	private GPIO a4Gpio;
	private GPIO a5Gpio;
	private GPIO a6Gpio;
	
	public static void main(String args[]) {
		System.out.println("Test_SP0256");
		Test_SP0256 app = new Test_SP0256();
		app.run();
	}

	public void run() {

		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);

			a1Gpio = new GPIO(pigpio, JPigpio.PI_GPIO17, JPigpio.PI_OUTPUT);
			a2Gpio = new GPIO(pigpio, JPigpio.PI_GPIO27, JPigpio.PI_OUTPUT);
			a3Gpio = new GPIO(pigpio, JPigpio.PI_GPIO22, JPigpio.PI_OUTPUT);
			a4Gpio = new GPIO(pigpio, JPigpio.PI_GPIO5, JPigpio.PI_OUTPUT);
			a5Gpio = new GPIO(pigpio, JPigpio.PI_GPIO6, JPigpio.PI_OUTPUT);
			a6Gpio = new GPIO(pigpio, JPigpio.PI_GPIO13, JPigpio.PI_OUTPUT);
			aldGpio = new GPIO(pigpio, JPigpio.PI_GPIO18, JPigpio.PI_OUTPUT);
			lrqGpio = new GPIO(pigpio, JPigpio.PI_GPIO23, JPigpio.PI_INPUT);
			SP0256 sp0256 = new SP0256(pigpio, aldGpio, lrqGpio, a1Gpio, a2Gpio, a3Gpio, a4Gpio, a5Gpio, a6Gpio);
			for (int i=0; i<64; i++) {
				sp0256.sayAlophone((byte)i);
			}
			pigpio.gpioTerminate();
			
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
} // End of class
// End of file