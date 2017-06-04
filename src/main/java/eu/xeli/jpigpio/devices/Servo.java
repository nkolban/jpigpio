package jpigpio.devices;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

/**
 * Control a servo at a high level.
 * <ol>
 * <li>Create an instance of the Servo class passing in:
 * <ul>
 * <li>pigpio</li>
 * <li>gpio</li>
 * <li>minVal</li>
 * <li>maxVal</li>
 * </ul>
 * </li>
 * </ol>
 * 
 * @author kolban
 *
 */
public class Servo {
	private JPigpio pigpio;
	private int minVal, maxVal, gpio;
	private int minPulseWidth, maxPulseWidth;

	/**
	 * Instantiate an instance of the servo class.
	 * 
	 * @param pigpio
	 *            The pigpio object to work against.
	 * @param gpio
	 *            The gpio that is to be used to control the servo.
	 * @param minVal
	 *            The minimum value of a control value.
	 * @param maxVal
	 *            The maximum value of a control value.
	 */
	public Servo(JPigpio pigpio, int gpio, int minVal, int maxVal) {
		this.pigpio = pigpio;
		this.gpio = gpio;
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.minPulseWidth = Pigpio.PI_MIN_SERVO_PULSEWIDTH;
		this.maxPulseWidth = Pigpio.PI_MAX_SERVO_PULSEWIDTH;
	} // End of constructor

	/**
	 * Set a control value
	 * @param value A value between the minimum and maximum values defined when the class was instantiated.
	 * @throws PigpioException
	 */
	public void setValue(int value) throws PigpioException {
		int mappedVal = Utils.mapToInt(value, minVal, maxVal, minPulseWidth, maxPulseWidth);
		System.out.println("Mapped val = " + mappedVal);
		pigpio.gpioServo(gpio, mappedVal);
	} // End of setValue

	/**
	 * Stop being a servo
	 * @throws PigpioException
	 */
	public void stop() throws PigpioException {
		pigpio.gpioServo(gpio, Pigpio.PI_SERVO_OFF);
	} // End of stop
} // End of Servo
// End of file