package jpigpio.devices;

import jpigpio.GPIO;
import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.WrongModeException;

public class SP0256 {
	private JPigpio pigpio;
	private GPIO aldGpio; // Output.   Latches data when low.  Data comes from a1-a6.
	private GPIO lrqGpio; // Input.  Goes high when input is full.  High when more data can be sent.
	private GPIO a1Gpio; // Output
	private GPIO a2Gpio; // Output
	private GPIO a3Gpio; // Output
	private GPIO a4Gpio; // Output
	private GPIO a5Gpio; // Output
	private GPIO a6Gpio; // Output
	
	
	public SP0256(JPigpio pigpio, GPIO aldGpio, GPIO lrqGpio, GPIO a1Gpio, GPIO a2Gpio, GPIO a3Gpio, GPIO a4Gpio, GPIO a5Gpio, GPIO a6Gpio ) throws PigpioException {
		this.pigpio = pigpio;
		this.aldGpio = aldGpio;
		this.lrqGpio = lrqGpio;
		this.a1Gpio = a1Gpio;
		this.a2Gpio = a2Gpio;
		this.a3Gpio = a3Gpio;
		this.a4Gpio = a4Gpio;
		this.a5Gpio = a5Gpio;
		this.a6Gpio = a6Gpio;
		
		if (aldGpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(aldGpio.getPin());
		}
		if (lrqGpio.getDirection() != JPigpio.PI_INPUT) {
			throw new WrongModeException(lrqGpio.getPin());
		}
		if (a1Gpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(a1Gpio.getPin());
		}
		if (a2Gpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(a2Gpio.getPin());
		}
		if (a3Gpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(a3Gpio.getPin());
		}
		if (a4Gpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(a4Gpio.getPin());
		}
		if (a5Gpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(a5Gpio.getPin());
		}
		if (a6Gpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(a6Gpio.getPin());
		}
		aldGpio.setValue(1); // Set the default to high
	} // End of constructor
	
	public void sayAlophone(byte value) throws PigpioException {
		a1Gpio.setValue(Utils.isSet(value, 0));
		a2Gpio.setValue(Utils.isSet(value, 1));
		a3Gpio.setValue(Utils.isSet(value, 2));
		a4Gpio.setValue(Utils.isSet(value, 3));
		a5Gpio.setValue(Utils.isSet(value, 4));
		a6Gpio.setValue(Utils.isSet(value, 5));
		while(lrqGpio.getValue() == true) {
			pigpio.gpioDelay(20000);
		}
		aldGpio.setValue(0);
		pigpio.gpioDelay(10000);
		aldGpio.setValue(1);
	} // End of sayAlophone
} // End of class
// End of file