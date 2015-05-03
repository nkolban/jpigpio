package jpigpio;

import jpigpio.impl.CommonPigpio;

public class Pigpio extends CommonPigpio {
	static {
		System.loadLibrary("JPigpioC");
	}

	@Override
	public native void gpioInitialize() throws PigpioException;

	@Override
	public native void gpioTerminate() throws PigpioException;

	@Override
	public native void gpioSetMode(int pin, int mode) throws PigpioException;

	@Override
	public native int gpioGetMode(int pin) throws PigpioException;

	@Override
	public native void gpioSetPullUpDown(int pin, int pud) throws PigpioException;

	@Override
	public native int gpioRead(int pin) throws PigpioException;

	@Override
	public native void gpioWrite(int pin, boolean value) throws PigpioException;

	@Override
	public native int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException;

	@Override
	public native void i2cClose(int handle) throws PigpioException;

	@Override
	public native int i2cReadDevice(int handle, byte[] data) throws PigpioException;

	@Override
	public native void i2cWriteDevice(int handle, byte[] data) throws PigpioException;

	@Override
	public native void gpioDelay(long delay) throws PigpioException;

	@Override
	public native long gpioTick() throws PigpioException;

	/**
	 * Set the pulse width of a specific GPIO. The pulse width is in microseconds with a value between 500 and 2500 or a value of 0 to switch the servo off.
	 * 
	 * @param gpio
	 *            The pin to use to control the servo.
	 * @param pulseWidth
	 *            The pulse width of the pulse (500-2500).
	 */
	@Override
	public native void gpioServo(int gpio, int pulseWidth) throws PigpioException;

	@Override
	public native void gpioSetAlertFunc(int pin, Alert alert) throws PigpioException;

	@Override
	public native void gpioTrigger(int gpio, long pulseLen, boolean level) throws PigpioException;

	/**
	 * Open an SPI channel.
	 * 
	 * @param spiChannel
	 *            The channel to open.
	 * @param spiBaudRate
	 *            The baud rate for transmition and receiption
	 * @param flags
	 *            Control flags
	 * @return A handle used in subsequent SPI API calls
	 * @throws PigpioException
	 */
	@Override
	public native int spiOpen(int spiChannel, int spiBaudRate, int flags) throws PigpioException;

	/**
	 * Close an SPI connection previously created with spiOpen().
	 * 
	 * @param handle
	 *            The handle to be closed.
	 * @throws PigpioException
	 */
	@Override
	public native void spiClose(int handle) throws PigpioException;

	/**
	 * Read data from SPI.
	 * 
	 * @param handle
	 *            The handle from which to read.
	 * @param data
	 *            An array into which to read data.
	 * @return The number of bytes actually read.
	 * @throws PigpioException
	 */
	@Override
	public native int spiRead(int handle, byte[] data) throws PigpioException;

	/**
	 * Write data to SPI.
	 * 
	 * @param handle
	 *            The handle into which to write.
	 * @param data
	 *            An array of data to write to SPI.
	 * @return The number of bytes actually written
	 * @throws PigpioException
	 */
	@Override
	public native int spiWrite(int handle, byte[] data) throws PigpioException;

	/**
	 * Write data to SPI and in parallel, read responses. The size of the txData and rxData arrays must be the same.
	 * 
	 * @param handle
	 *            The handle into which to write.
	 * @param txData
	 *            An array of data to write.
	 * @param rxData
	 *            An array of data to read.
	 * @return The number of bytes actually transferred.
	 * @throws PigpioException
	 */
	@Override
	public native int spiXfer(int handle, byte[] txData, byte[] rxData) throws PigpioException;

} // End of class
// End of file