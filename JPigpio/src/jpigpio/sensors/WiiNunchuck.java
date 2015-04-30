package jpigpio.sensors;

import jpigpio.JPigpio;
import jpigpio.PigpioException;

/**
 * Wrapper for WiiNunchuck.  The Wii Nunchuck is an I2C device and hence has to be connected
 * to the SDA and SCL pins of the Pi.
 * 
 * To use this class:
 * <ul>
 * <li>Initialize Pigpio</li>
 * <li>Call initialize()</li>
 * <li>Call readValue()</li>
 * <li>get the data fields ...</li>
 * </ul>
 * 
 * @author Neil Kolban
 * @date 2015-04-30
 *
 */
public class WiiNunchuck {
	private final int NUNCHUCK_DEVICE = 0x52;
	private final int PI_I2CBUS = 1;
	private JPigpio pigpio;
	private int handle;
	private int joyX;
	private int joyY;
	private int accelX;
	private int accelY;
	private int accelZ;
	private boolean buttonC;
	private boolean buttonZ;
	
	public WiiNunchuck(JPigpio pigpio) {
		this.pigpio = pigpio;
	} // End of constructor
	
	/**
	 * Initialize the Nunchuck
	 * @throws PigpioException
	 */
	public void initialize() throws PigpioException {
		handle = pigpio.i2cOpen(PI_I2CBUS, NUNCHUCK_DEVICE);
		byte data[] = { 0x40, 0x00 };
		pigpio.i2cWriteDevice(handle, data);
		pigpio.gpioDelay(500);
	} // End of initialize
	
	public void close() throws PigpioException {
		pigpio.i2cClose(handle);
	} // End of close
	
	/**
	 * Read a value from the Wii Nunchuck.
	 * @throws PigpioException
	 */
	public void readValue() throws PigpioException {
		byte data2[] = { 0x00 };
		pigpio.i2cWriteDevice(handle, data2);
		pigpio.gpioDelay(200);
		byte data3[] = new byte[6];
		pigpio.i2cReadDevice(handle, data3);

		byte bytes[] = data3;
		// (d) 1 2 4 8 16 32 64 128
		// (h) 1 2 4 8 10 20 40 80
		joyX = bytes[0];
		joyY = bytes[1];
		accelX = (bytes[2] << 2) | ((bytes[5] & 0xc0) >> 6);
		accelY = (bytes[3] << 2) | ((bytes[5] & 0x30) >> 4);
		accelZ = (bytes[4] << 2) | ((bytes[5] & 0x0c) >> 2);
		buttonC = ((bytes[5] & 0x02) >> 1) == 1;
		buttonZ = (bytes[5] & 0x01) == 1;
	} // End of readValue

	public int getJoyX() {
		return joyX;
	}

	public int getJoyY() {
		return joyY;
	}

	public int getAccelX() {
		return accelX;
	}

	public int getAccelY() {
		return accelY;
	}

	public int getAccelZ() {
		return accelZ;
	}

	public boolean isButtonC() {
		return buttonC;
	}

	public boolean isButtonZ() {
		return buttonZ;
	}
	
	@Override
	public String toString() {
		return String.format("joyX=%x joyY=%x accelX=%x accelY=%x accelZ=%x buttonC=%x buttonZ=%x", joyX, joyY, accelX, accelY, accelZ, buttonC, buttonZ);
	} // End of toString
} // End of class
// End of file