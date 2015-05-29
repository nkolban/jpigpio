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

	/**
	 * Retrieve the mode of the given gpio
	 * @param gpio The gpio to retrieve the mode
	 * @return The mode of the gpio
	 * @throws PigpioException
	 */
	public int gpioGetMode(int gpio) throws PigpioException;

	/**
	 * Set the specified gpio to the level specified by level for the duration specified
	 * by pulseLen and then set the gpio back to !level.
	 * @param gpio The GPIO pin to pulse.
	 * @param pulseLen The duration in useconds to hold the pulse.
	 * @param level The level to target the pulse.
	 * @throws PigpioException
	 */
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
	public boolean gpioRead(int gpio) throws PigpioException;

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

	/**
	 * Shift out a byte of data to a given pin.  Note that this function is implemented in
	 * Javacode.
	 * @param gpioData The gpio to which to write the data.
	 * @param gpioClock The clock gpio to pulse.
	 * @param bitOrder The order of the bits.  Either PI_LSBFIRST or PI_MSBFIRST.
	 * @param value The value of the byte to write.
	 * @throws PigpioException
	 */
	public void gpioShiftOut(int gpioData, int gpioClock, boolean bitOrder, byte value) throws PigpioException;
	public void gpioShiftOut(GPIO gpioData, GPIO gpioClock, boolean bitOrder, byte value) throws PigpioException;
	
	/**
	 * Shift out a byte of data to a given pin.  Note that this function is implemented in
	 * Javacode.
	 * @param gpioData The gpio to which to write the data.
	 * @param gpioClock The clock gpio to pulse.
	 * @param clockLevel The value of the clock pulse
	 * @param bitOrder The order of the bits
	 * @param value The value of the byte to write.
	 * @throws PigpioException
	 */	
	public void gpioShiftOut(int gpioData, int gpioClock, boolean clockLevel,  boolean bitOrder, byte value) throws PigpioException;
	public void gpioShiftOut(GPIO gpioData, GPIO gpioClock, boolean clockLevel,  boolean bitOrder, byte value) throws PigpioException;


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
	
	public void setDebug(boolean flag) throws PigpioException;
	
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
	public long gpioxPulseAndWait(int outGpio, int inGpio, long waitDuration, long pulseHoldDuration, boolean pulseLow) throws PigpioException;


	public static final int PI_GPIO2 = 2;
	public static final int PI_GPIO3 = 3;
	public static final int PI_GPIO4 = 4;
	public static final int PI_GPIO5 = 5;
	public static final int PI_GPIO6 = 6;
	public static final int PI_GPIO7 = 7;
	public static final int PI_GPIO8 = 8;
	public static final int PI_GPIO9 = 9;
	public static final int PI_GPIO10 = 10;
	public static final int PI_GPIO11 = 11;
	public static final int PI_GPIO12 = 12;
	public static final int PI_GPIO13 = 13;
	public static final int PI_GPIO14 = 14;
	public static final int PI_GPIO15 = 15;
	public static final int PI_GPIO16 = 16;
	public static final int PI_GPIO17 = 17;
	public static final int PI_GPIO18 = 18;
	public static final int PI_GPIO19 = 19;
	public static final int PI_GPIO20 = 20;
	public static final int PI_GPIO21 = 21;
	public static final int PI_GPIO22 = 22;
	public static final int PI_GPIO23 = 23;
	public static final int PI_GPIO24 = 24;
	public static final int PI_GPIO25 = 25;
	public static final int PI_GPIO26 = 26;
	public static final int PI_GPIO27 = 27;

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
	
	public static final int PI_SPI_CE0 = PI_GPIO8;
	public static final int PI_SPI_CE1 = PI_GPIO7;
	public static final int PI_SPI_MOSI = PI_GPIO10;
	public static final int PI_SPI_MISO = PI_GPIO9;
	public static final int PI_SPI_SCLK = PI_GPIO11;
	
	/* I2C */
	public static final int PI_I2C1_SDA = PI_GPIO2;
	public static final int PI_I2C1_SCL = PI_GPIO3;
	

} // End of interface
// End of file