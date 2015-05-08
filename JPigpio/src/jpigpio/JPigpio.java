package jpigpio;

/**
 * The exposed pigpio functions as Java methods.
 *
 */
public interface JPigpio {

	public void gpioInitialize() throws PigpioException;

	public void gpioTerminate() throws PigpioException;

	/**
	 * Set the mode of the gpio
	 * 
	 * @param gpio
	 *            The gpio pin to set
	 * @param mode
	 *            The mode of the pin. One of PI_INPUT or PI_OUTPUT
	 * @throws PigpioException
	 */
	public void gpioSetMode(int gpio, int mode) throws PigpioException;

	public int gpioGetMode(int gpio) throws PigpioException;

	public void gpioTrigger(int gpio, long pulseLen, boolean level) throws PigpioException;

	public void gpioSetPullUpDown(int gpio, int pud) throws PigpioException;

	/**
	 * Retrieve the state of the gpio
	 * 
	 * @param gpio
	 *            The gpio pin to retrieve
	 * @return The state of the gpio, one of PI_HIGH or PI_LOW (or equivalents)
	 * @throws PigpioException
	 */
	public int gpioRead(int gpio) throws PigpioException;

	/**
	 * Set the state of the gpio
	 * 
	 * @param gpio
	 *            The gpio pin to set
	 * @param value
	 *            The desired value of the new pin state. One of PI_HIGH or PI_LOW (or equivalents)
	 * @throws PigpioException
	 */
	public void gpioWrite(int gpio, boolean value) throws PigpioException;

	public void gpioShiftOut(int gpioData, int gpioClock, boolean bitOrder, int value) throws PigpioException;

	/**
	 * Set the pulse width of a specific GPIO. The pulse width is in microseconds with a value between 500 and 2500 or a value of 0 to switch the servo off.
	 * 
	 * @param gpio
	 *            The pin to use to control the servo.
	 * @param pulseWidth
	 *            The pulse width of the pulse (500-2500).
	 */
	public void gpioServo(int gpio, int pulseWidth) throws PigpioException;

	/**
	 * Delay for the specified number of microseconds
	 * 
	 * @param delay
	 *            The number of microseconds for which to block
	 */
	public void gpioDelay(long delay) throws PigpioException;
	
	public void gpioDelay(long delay, int type) throws PigpioException;

	public long gpioTick() throws PigpioException;

	public int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException;

	public void i2cClose(int handle) throws PigpioException;

	public int i2cReadDevice(int handle, byte data[]) throws PigpioException;

	public void i2cWriteDevice(int handle, byte data[]) throws PigpioException;

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
	public int spiOpen(int channel, int baudRate, int flags) throws PigpioException;

	/**
	 * Close an SPI connection previously created with spiOpen().
	 * @param handle The handle to be closed.
	 * @throws PigpioException
	 */
	public void spiClose(int handle) throws PigpioException;

	/**
	 * Read data from SPI.
	 * @param handle The handle from which to read.
	 * @param data An array into which to read data.
	 * @return The number of bytes actually read.
	 * @throws PigpioException
	 */
	public int spiRead(int handle, byte data[]) throws PigpioException;
	
	/**
	 * Write data to SPI.
	 * @param handle The handle into which to write.
	 * @param data An array of data to write to SPI.
	 * @return The number of bytes actually written
	 * @throws PigpioException
	 */
	public int spiWrite(int handle, byte data[]) throws PigpioException;
	
	/**
	 * Write data to SPI and in parallel, read responses.  The size of the txData and rxData arrays must
	 * be the same.
	 * @param handle The handle into which to write.
	 * @param txData An array of data to write.
	 * @param rxData An array of data to read.
	 * @return The number of bytes actually transferred.
	 * @throws PigpioException
	 */
	public int spiXfer(int handle, byte txData[], byte rxData[]) throws PigpioException;

	public void gpioSetAlertFunc(int gpio, Alert alert) throws PigpioException;
	
	public void setDebug(boolean flag);

	/* level: 0-1 */

	public static final int PI_OFF = 0;
	public static final int PI_ON = 1;

	public static final int PI_CLEAR = 0;
	public static final int PI_SET = 1;

	public static final boolean PI_LOW = false;
	public static final boolean PI_HIGH = true;

	/* level: only reported for gpio time-out, see gpioSetWatchdog */

	public static final int PI_TIMEOUT = 2;
	
	public static final int PI_MICROSECONDS = 0;
	public static final int PI_MILLISECONDS = 1;
	public static final int PI_SECONDS = 2;

	/* mode: 0-7 */

	public static final int PI_INPUT = 0;
	public static final int PI_OUTPUT = 1;
	public static final int PI_ALT0 = 4;
	public static final int PI_ALT1 = 5;
	public static final int PI_ALT2 = 6;
	public static final int PI_ALT3 = 7;
	public static final int PI_ALT4 = 3;
	public static final int PI_ALT5 = 2;

	/* pud: 0-2 */

	public static final int PI_PUD_OFF = 0;
	public static final int PI_PUD_DOWN = 1;
	public static final int PI_PUD_UP = 2;

	/* dutycycle: 0-range */

	public static final int PI_DEFAULT_DUTYCYCLE_RANGE = 255;

	/* range: 25-40000 */

	public static final int PI_MIN_DUTYCYCLE_RANGE = 25;
	public static final int PI_MAX_DUTYCYCLE_RANGE = 40000;

	/* pulsewidth: 0, 500-2500 */

	public static final int PI_SERVO_OFF = 0;
	public static final int PI_MIN_SERVO_PULSEWIDTH = 500;
	public static final int PI_MAX_SERVO_PULSEWIDTH = 2500;

	/* hardware PWM */

	public static final int PI_HW_PWM_MIN_FREQ = 1;
	public static final int PI_HW_PWM_MAX_FREQ = 125000000;
	public static final int PI_HW_PWM_RANGE = 1000000;

	/* hardware clock */

	public static final int PI_HW_CLK_MIN_FREQ = 4689;
	public static final int PI_HW_CLK_MAX_FREQ = 250000000;

	public static final boolean PI_MSBFIRST = true;
	public static final boolean PI_LSBFIRST = false;
	
	/* SPI */
	public static final int PI_SPI_MODE0 = 0b00;
	public static final int PI_SPI_MODE1 = 0b01;
	public static final int PI_SPI_MODE2 = 0b10;
	public static final int PI_SPI_MODE3 = 0b11;
	
	public static final int PI_SPI_BAUD_125KHZ = 125000;
	public static final int PI_SPI_BAUD_250KHZ = JPigpio.PI_SPI_BAUD_125KHZ * 2;
	public static final int PI_SPI_BAUD_500KHZ = JPigpio.PI_SPI_BAUD_250KHZ * 2;
	public static final int PI_SPI_BAUD_1MHZ = JPigpio.PI_SPI_BAUD_500KHZ * 2;
	public static final int PI_SPI_BAUD_2MHZ = JPigpio.PI_SPI_BAUD_1MHZ * 2;
	public static final int PI_SPI_BAUD_4MHZ = JPigpio.PI_SPI_BAUD_2MHZ * 2;
	public static final int PI_SPI_BAUD_8MHZ = JPigpio.PI_SPI_BAUD_4MHZ * 2;
	
	public static final int PI_SPI_CHANNEL0 = 0;
	public static final int PI_SPI_CHANNEL1 = 1;

} // End of interface
// End of file