package jpigpio;

import jpigpio.impl.CommonPigpio;

import java.util.ArrayList;

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
	public native boolean gpioRead(int pin) throws PigpioException;

	@Override
	public native void gpioWrite(int pin, boolean value) throws PigpioException;

	// ################ NOTIFICATIONS

	@Override
	public int notifyOpen() throws PigpioException{
		throw new NotImplementedException();
	}

	@Override
	public int notifyBegin(int handle, int bits) throws PigpioException{
		throw new NotImplementedException();
	}

	@Override
	public int notifyPause(int handle) throws PigpioException{
		throw new NotImplementedException();
	}

	@Override
	public int notifyClose(int handle) throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int setWatchdog(int userGpio, int timeout) throws PigpioException{
		throw new NotImplementedException();
	}

	// ##################### WAVES

	/**
	 * This function clears all waveforms and any data added by calls to the wave_add_* functions.
	 *
	 * @return The return code from close.
	 */
	@Override
	public int waveClear() throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveAddGeneric(ArrayList<Pulse> pulses) throws PigpioException{
		throw new NotImplementedException();
	}

	@Override
	public int waveAddSerial(int userGpio, int baud, byte[] data, int offset, int bbBits, int bbStop) throws PigpioException{
		throw new NotImplementedException();
	}

	@Override
	public int waveAddNew() throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveTxBusy() throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveTxStop() throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveCreate() throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveDelete(int waveId) throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveSendOnce(int waveId) throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public int waveSendRepeat(int waveId) throws PigpioException {
		throw new NotImplementedException();
	}


	// ################ I2C

	/**
	 * Open a connection to the i2c
	 *
	 * @param i2cBus
	 *            The id of the bus (1 for pi)
	 * @param i2cAddr
	 *            The address of the device on the bus
	 * @return The handle for the device on the bus.
	 */
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

	@Override
	public long getCurrentTick() throws PigpioException{
		return gpioTick();
	}


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
	 * @param channel The channel to open.
	 * @param baudRate The baud rate for transmission and reception.  Some constants are provided:
	 * <ul>
	 * <li>PI_SPI_BAUD_125KHZ</li>
	 * <li>PI_SPI_BAUD_250KHZ</li>
	 * <li>PI_SPI_BAUD_500KHZ</li>
	 * <li>PI_SPI_BAUD_1MHZ</li>
	 * <li>PI_SPI_BAUD_2MHZ</li>
	 * <li>PI_SPI_BAUD_4MHZ</li>
	 * <li>PI_SPI_BAUD_8MHZ</li>
	 * </ul>
	 * @param flags Control flags.  The flags can include:
	 * 
	 * @return A handle used in subsequent SPI API calls
	 * @throws PigpioException
	 */
	@Override
	public native int spiOpen(int channel, int baudRate, int flags) throws PigpioException;

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

	@Override
	/**
	 * Set whether or not debugging is enabled. True is enabled.
	 */
	public native void setDebug(boolean flag);

	/**
	 * Pulse a named pin and then wait for a response on a different pin.  The output pin should already
	 * have a PI_OUTPUT mode and the input pin should already have a PI_INPUT mode.  The wait duration is in
	 * microseconds.  The pulse hold duration is how long (in microseconds) the pulse should be held for.  The
	 * default is to pulse the output high and then return low however if the pulseLow flag is set the inverse
	 * will happen (pulse the output low and then return high).
	 * 
	 * The return is how long we waited for the pulse measured in microseconds.  If no response is received, we
	 * return -1 to indicate a timeout.
	 * @param outGpio The pin on which the output pulse will occur.
	 * @param inGpio The pin on which the input pulse will be sought.
	 * @param waitDuration The maximum time to wait in microseconds.
	 * @param pulseHoldDuration The time to hold the output pulse in microseconds.
	 * @param pulseLow True if the pulse should be a low pulse otherwise a high pulse will be sent.
	 * @return The time in microseconds waiting for a pulse or -1 to signfify a timeout.
	 */
	@Override
	public native long gpioxPulseAndWait(int outGpio, int inGpio, long waitDuration, long pulseHoldDuration, boolean pulseLow) throws PigpioException;

	@Override
	public void addCallback(Callback cb) throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public void removeCallback(Callback cb) throws PigpioException{
		throw new NotImplementedException();
	}

} // End of class
// End of file