package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.devices.LCD;

public class Test_LCD {
	private int registerSelectGpio = JPigpio.PI_GPIO17;
	private int readWriteGpio = JPigpio.PI_GPIO27;
	private int enableGpio = JPigpio.PI_GPIO22;
	private int db4Gpio = JPigpio.PI_GPIO5;
	private int db5Gpio = JPigpio.PI_GPIO6;
	private int db6Gpio = JPigpio.PI_GPIO13;
	private int db7Gpio = JPigpio.PI_GPIO19;


	public static void main(String args[]) {
		System.out.println("Test_LCD");
		Test_LCD app = new Test_LCD();
		app.run();
	}

	public void run() {

		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			Utils.addShutdown(pigpio);
			pigpio.gpioSetMode(registerSelectGpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(readWriteGpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(enableGpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(db4Gpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(db5Gpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(db6Gpio, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(db7Gpio, JPigpio.PI_OUTPUT);
			LCD lcd = new LCD(pigpio, registerSelectGpio, readWriteGpio, enableGpio, db4Gpio, db5Gpio, db6Gpio, db7Gpio);
			//lcd.display(false,  false,  false);
			lcd.clear();
			lcd.write("Hi Steph!");
			
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
} // End of class
// End of file