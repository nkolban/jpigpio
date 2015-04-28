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
	 * @param gpio The gpio pin to set
	 * @param mode The mode of the pin.  One of PI_INPUT or PI_OUTPUT
	 * @throws PigpioException
	 */
	public void gpioSetMode(int gpio, int mode) throws PigpioException;

	public int gpioGetMode(int gpio) throws PigpioException;
	
	public void gpioTrigger(int gpio, long pulseLen, boolean level) throws PigpioException;

	public void gpioSetPullUpDown(int gpio, int pud) throws PigpioException;

	/**
	 * Retrieve the state of the gpio
	 * @param gpio The gpio pin to retrieve
	 * @return The state of the gpio, one of PI_HIGH or PI_LOW (or equivalents)
	 * @throws PigpioException
	 */
	public int gpioRead(int gpio) throws PigpioException;

	/**
	 * Set the state of the gpio
	 * @param gpio The gpio pin to set
	 * @param value The desired value of the new pin state.  One of PI_HIGH or PI_LOW (or equivalents)
	 * @throws PigpioException
	 */
	public void gpioWrite(int gpio, boolean value) throws PigpioException;
	
	public void gpioShiftOut(int gpioData, int gpioClock, boolean bitOrder, int value) throws PigpioException;

	public void gpioServo(int gpio, int pulseWidth) throws PigpioException;

	/**
	 * Delay for the specified number of microseconds
	 * @param delay The number of microseconds for which to block
	 */
	public void gpioDelay(long delay) throws PigpioException;

	public long gpioTick() throws PigpioException;

	public int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException;

	public void i2cClose(int handle) throws PigpioException;

	public int i2cReadDevice(int handle, byte data[]) throws PigpioException;

	public void i2cWriteDevice(int handle, byte data[]) throws PigpioException;

	public void gpioSetAlertFunc(int gpio, Alert alert) throws PigpioException;

	/* level: 0-1 */

	public static final int PI_OFF = 0;
	public static final int PI_ON = 1;

	public static final int PI_CLEAR = 0;
	public static final int PI_SET = 1;

	public static final boolean PI_LOW = false;
	public static final boolean PI_HIGH = true;

	/* level: only reported for gpio time-out, see gpioSetWatchdog */

	public static final int PI_TIMEOUT = 2;

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

} // End of interface
// End of file