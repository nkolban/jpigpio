package jpigpio;

public class PigpioException extends Exception {

	private int rc = -99999999;
	/**
	 * 
	 */
	private static final long serialVersionUID = 443595760654129068L;

	public PigpioException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PigpioException(int rc) {
		super();
		this.rc = rc;
	}

	public PigpioException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}

	public PigpioException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public PigpioException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public PigpioException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getMessage() {
		return String.format("rc=%d", rc);
	}

	public static final int PI_INIT_FAILED = -1; // gpioInitialise failed
	public static final int PI_BAD_USER_GPIO = -2; // gpio not 0-31
	public static final int PI_BAD_GPIO = -3; // gpio not 0-53
	public static final int PI_BAD_MODE = -4; // mode not 0-7
	public static final int PI_BAD_LEVEL = -5; // level not 0-1
	public static final int PI_BAD_PUD = -6; // pud not 0-2
	public static final int PI_BAD_PULSEWIDTH = -7; // pulsewidth not 0 or 500-2500
	public static final int PI_BAD_DUTYCYCLE = -8; // dutycycle outside set range
	public static final int PI_BAD_TIMER = -9; // timer not 0-9
	public static final int PI_BAD_MS = -10; // ms not 10-60000
	public static final int PI_BAD_TIMETYPE = -11; // timetype not 0-1
	public static final int PI_BAD_SECONDS = -12; // seconds < 0
	public static final int PI_BAD_MICROS = -13; // micros not 0-999999
	public static final int PI_TIMER_FAILED = -14; // gpioSetTimerFunc failed
	public static final int PI_BAD_WDOG_TIMEOUT = -15; // timeout not 0-60000
	public static final int PI_NO_ALERT_FUNC = -16; // DEPRECATED
	public static final int PI_BAD_CLK_PERIPH = -17; // clock peripheral not 0-1
	public static final int PI_BAD_CLK_SOURCE = -18; // DEPRECATED
	public static final int PI_BAD_CLK_MICROS = -19; // clock micros not 1, 2, 4, 5, 8, or 10
	public static final int PI_BAD_BUF_MILLIS = -20; // buf millis not 100-10000
	public static final int PI_BAD_DUTYRANGE = -21; // dutycycle range not 25-40000
	public static final int PI_BAD_DUTY_RANGE = -21; // DEPRECATED (use PI_BAD_DUTYRANGE)
	public static final int PI_BAD_SIGNUM = -22; // signum not 0-63
	public static final int PI_BAD_PATHNAME = -23; // can't open pathname
	public static final int PI_NO_HANDLE = -24; // no handle available
	public static final int PI_BAD_HANDLE = -25; // unknown handle
	public static final int PI_BAD_IF_FLAGS = -26; // ifFlags > 3
	public static final int PI_BAD_CHANNEL = -27; // DMA channel not 0-14
	public static final int PI_BAD_PRIM_CHANNEL = -27; // DMA primary channel not 0-14
	public static final int PI_BAD_SOCKET_PORT = -28; // socket port not 1024-32000
	public static final int PI_BAD_FIFO_COMMAND = -29; // unrecognized fifo command
	public static final int PI_BAD_SECO_CHANNEL = -30; // DMA secondary channel not 0-6
	public static final int PI_NOT_INITIALISED = -31; // function called before gpioInitialise
	public static final int PI_INITIALISED = -32; // function called after gpioInitialise
	public static final int PI_BAD_WAVE_MODE = -33; // waveform mode not 0-1
	public static final int PI_BAD_CFG_INTERNAL = -34; // bad parameter in gpioCfgInternals call
	public static final int PI_BAD_WAVE_BAUD = -35; // baud rate not 50-250K(RX)/50-1M(TX)
	public static final int PI_TOO_MANY_PULSES = -36; // waveform has too many pulses
	public static final int PI_TOO_MANY_CHARS = -37; // waveform has too many chars
	public static final int PI_NOT_SERIAL_GPIO = -38; // no serial read in progress on gpio
	public static final int PI_BAD_SERIAL_STRUC = -39; // bad (null) serial structure parameter
	public static final int PI_BAD_SERIAL_BUF = -40; // bad (null) serial buf parameter
	public static final int PI_NOT_PERMITTED = -41; // gpio operation not permitted
	public static final int PI_SOME_PERMITTED = -42; // one or more gpios not permitted
	public static final int PI_BAD_WVSC_COMMND = -43; // bad WVSC subcommand
	public static final int PI_BAD_WVSM_COMMND = -44; // bad WVSM subcommand
	public static final int PI_BAD_WVSP_COMMND = -45; // bad WVSP subcommand
	public static final int PI_BAD_PULSELEN = -46; // trigger pulse length not 1-100
	public static final int PI_BAD_SCRIPT = -47; // invalid script
	public static final int PI_BAD_SCRIPT_ID = -48; // unknown script id
	public static final int PI_BAD_SER_OFFSET = -49; // add serial data offset > 30 minutes
	public static final int PI_GPIO_IN_USE = -50; // gpio already in use
	public static final int PI_BAD_SERIAL_COUNT = -51; // must read at least a byte at a time
	public static final int PI_BAD_PARAM_NUM = -52; // script parameter id not 0-9
	public static final int PI_DUP_TAG = -53; // script has duplicate tag
	public static final int PI_TOO_MANY_TAGS = -54; // script has too many tags
	public static final int PI_BAD_SCRIPT_CMD = -55; // illegal script command
	public static final int PI_BAD_VAR_NUM = -56; // script variable id not 0-149
	public static final int PI_NO_SCRIPT_ROOM = -57; // no more room for scripts
	public static final int PI_NO_MEMORY = -58; // can't allocate temporary memory
	public static final int PI_SOCK_READ_FAILED = -59; // socket read failed
	public static final int PI_SOCK_WRIT_FAILED = -60; // socket write failed
	public static final int PI_TOO_MANY_PARAM = -61; // too many script parameters (> 10)
	public static final int PI_NOT_HALTED = -62; // script already running or failed
	public static final int PI_BAD_TAG = -63; // script has unresolved tag
	public static final int PI_BAD_MICS_DELAY = -64; // bad MICS delay (too large)
	public static final int PI_BAD_MILS_DELAY = -65; // bad MILS delay (too large)
	public static final int PI_BAD_WAVE_ID = -66; // non existent wave id
	public static final int PI_TOO_MANY_CBS = -67; // No more CBs for waveform
	public static final int PI_TOO_MANY_OOL = -68; // No more OOL for waveform
	public static final int PI_EMPTY_WAVEFORM = -69; // attempt to create an empty waveform
	public static final int PI_NO_WAVEFORM_ID = -70; // no more waveforms
	public static final int PI_I2C_OPEN_FAILED = -71; // can't open I2C device
	public static final int PI_SER_OPEN_FAILED = -72; // can't open serial device
	public static final int PI_SPI_OPEN_FAILED = -73; // can't open SPI device
	public static final int PI_BAD_I2C_BUS = -74; // bad I2C bus
	public static final int PI_BAD_I2C_ADDR = -75; // bad I2C address
	public static final int PI_BAD_SPI_CHANNEL = -76; // bad SPI channel
	public static final int PI_BAD_FLAGS = -77; // bad i2c/spi/ser open flags
	public static final int PI_BAD_SPI_SPEED = -78; // bad SPI speed
	public static final int PI_BAD_SER_DEVICE = -79; // bad serial device name
	public static final int PI_BAD_SER_SPEED = -80; // bad serial baud rate
	public static final int PI_BAD_PARAM = -81; // bad i2c/spi/ser parameter
	public static final int PI_I2C_WRITE_FAILED = -82; // i2c write failed
	public static final int PI_I2C_READ_FAILED = -83; // i2c read failed
	public static final int PI_BAD_SPI_COUNT = -84; // bad SPI count
	public static final int PI_SER_WRITE_FAILED = -85; // ser write failed
	public static final int PI_SER_READ_FAILED = -86; // ser read failed
	public static final int PI_SER_READ_NO_DATA = -87; // ser read no data available
	public static final int PI_UNKNOWN_COMMAND = -88; // unknown command
	public static final int PI_SPI_XFER_FAILED = -89; // spi xfer/read/write failed
	public static final int PI_BAD_POINTER = -90; // bad (NULL) pointer
	public static final int PI_NO_AUX_SPI = -91; // need a A+/B+/Pi2 for auxiliary SPI
	public static final int PI_NOT_PWM_GPIO = -92; // gpio is not in use for PWM
	public static final int PI_NOT_SERVO_GPIO = -93; // gpio is not in use for servo pulses
	public static final int PI_NOT_HCLK_GPIO = -94; // gpio has no hardware clock
	public static final int PI_NOT_HPWM_GPIO = -95; // gpio has no hardware PWM
	public static final int PI_BAD_HPWM_FREQ = -96; // hardware PWM frequency not 1-125M
	public static final int PI_BAD_HPWM_DUTY = -97; // hardware PWM dutycycle not 0-1M
	public static final int PI_BAD_HCLK_FREQ = -98; // hardware clock frequency not 4689-250M
	public static final int PI_BAD_HCLK_PASS = -99; // need password to use hardware clock 1
	public static final int PI_HPWM_ILLEGAL = -100; // illegal, PWM in use for main clock
	public static final int PI_BAD_DATABITS = -101; // serial data bits not 1-32
	public static final int PI_BAD_STOPBITS = -102; // serial (half) stop bits not 2-8
	public static final int PI_MSG_TOOBIG = -103; // socket/pipe message too big
	public static final int PI_BAD_MALLOC_MODE = -104; // bad memory allocation mode
	public static final int PI_TOO_MANY_PARTS = -105; // too many I2C transaction parts
	public static final int PI_BAD_I2C_PART = -106; // a combined I2C transaction failed

	public static final int PI_PIGIF_ERR_0 = -2000;
	public static final int PI_PIGIF_ERR_99 = -2099;

	public static final int PI_CUSTOM_ERR_0 = -3000;
	public static final int PI_CUSTOM_ERR_999 = -3999;
} // End of class
// End of file