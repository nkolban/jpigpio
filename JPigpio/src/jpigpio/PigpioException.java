package jpigpio;

/**
 * A clas sthat defines the exceptions that can be thrown by Pigpio.
 *
 */
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

	public PigpioException(int rc, String msg) {
		super(msg);
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
		return String.format("(%d) "+errorText(rc), rc);
	}
	
	/**
	 * Retrieve the error code that was returned by the underlying Pigpio call.
	 * @return The error code that was returned by the underlying Pigpio call.
	 */
	public int getErrorCode() {
		return rc;
	} // End of getErrorCode

	// Public constants for the error codes that can be thrown by Pigpio
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
	public static final int PI_TOO_MANY_SEGS = -105; // too many I2C transaction parts
	public static final int PI_BAD_I2C_SEG = -106; // a combined I2C transaction failed
	public static final int PI_BAD_SMBUS_CMD    =-107;
	public static final int PI_NOT_I2C_GPIO     =-108;
	public static final int PI_BAD_I2C_WLEN     =-109;
	public static final int PI_BAD_I2C_RLEN     =-110;
	public static final int PI_BAD_I2C_CMD      =-111;
	public static final int PI_BAD_I2C_BAUD     =-112;
	public static final int PI_CHAIN_LOOP_CNT   =-113;
	public static final int PI_BAD_CHAIN_LOOP   =-114;
	public static final int PI_CHAIN_COUNTER    =-115;
	public static final int PI_BAD_CHAIN_CMD    =-116;
	public static final int PI_BAD_CHAIN_DELAY  =-117;
	public static final int PI_CHAIN_NESTING    =-118;
	public static final int PI_CHAIN_TOO_BIG    =-119;
	public static final int PI_DEPRECATED       =-120;
	public static final int PI_BAD_SER_INVERT   =-121;
	public static final int PI_BAD_EDGE        =-122;
	public static final int PI_BAD_ISR_INIT    =-123;
	public static final int PI_BAD_FOREVER      =-124;
	public static final int PI_BAD_FILTER       =-125;

	public static final int PI_PIGIF_ERR_0 = -2000;
	public static final int PI_PIGIF_ERR_99 = -2099;

	public static final int PI_CUSTOM_ERR_0 = -3000;
	public static final int PI_CUSTOM_ERR_999 = -3999;
	
	class ErrMsg {
		int code;
		String msg;
		
		ErrMsg(int code, String msg){
			this.code = code;
			this.msg = msg;
		}
	}

	ErrMsg[] errors = new ErrMsg[] {
			new ErrMsg(PI_INIT_FAILED      , "pigpio initialisation failed"),
			new ErrMsg(PI_BAD_USER_GPIO     , "GPIO not 0-31"),
			new ErrMsg(PI_BAD_GPIO          , "GPIO not 0-53"),
			new ErrMsg(PI_BAD_MODE          , "mode not 0-7"),
			new ErrMsg(PI_BAD_LEVEL         , "level not 0-1"),
			new ErrMsg(PI_BAD_PUD           , "pud not 0-2"),
			new ErrMsg(PI_BAD_PULSEWIDTH    , "pulsewidth not 0 or 500-2500"),
			new ErrMsg(PI_BAD_DUTYCYCLE     , "dutycycle not 0-range (default 255)"),
			new ErrMsg(PI_BAD_TIMER        , "timer not 0-9"),
			new ErrMsg(PI_BAD_MS           , "ms not 10-60000"),
			new ErrMsg(PI_BAD_TIMETYPE     , "timetype not 0-1"),
			new ErrMsg(PI_BAD_SECONDS      , "seconds < 0"),
			new ErrMsg(PI_BAD_MICROS       , "micros not 0-999999"),
			new ErrMsg(PI_TIMER_FAILED     , "gpioSetTimerFunc failed"),
			new ErrMsg(PI_BAD_WDOG_TIMEOUT  , "timeout not 0-60000"),
			new ErrMsg(PI_NO_ALERT_FUNC    , "DEPRECATED"),
			new ErrMsg(PI_BAD_CLK_PERIPH   , "clock peripheral not 0-1"),
			new ErrMsg(PI_BAD_CLK_SOURCE   , "DEPRECATED"),
			new ErrMsg(PI_BAD_CLK_MICROS   , "clock micros not 1, 2, 4, 5, 8, or 10"),
			new ErrMsg(PI_BAD_BUF_MILLIS   , "buf millis not 100-10000"),
			new ErrMsg(PI_BAD_DUTYRANGE     , "dutycycle range not 25-40000"),
			new ErrMsg(PI_BAD_SIGNUM       , "signum not 0-63"),
			new ErrMsg(PI_BAD_PATHNAME     , "can't open pathname"),
			new ErrMsg(PI_NO_HANDLE         , "no handle available"),
			new ErrMsg(PI_BAD_HANDLE        , "unknown handle"),
			new ErrMsg(PI_BAD_IF_FLAGS     , "ifFlags > 3"),
			new ErrMsg(PI_BAD_CHANNEL      , "DMA channel not 0-14"),
			new ErrMsg(PI_BAD_SOCKET_PORT  , "socket port not 1024-30000"),
			new ErrMsg(PI_BAD_FIFO_COMMAND , "unknown fifo command"),
			new ErrMsg(PI_BAD_SECO_CHANNEL , "DMA secondary channel not 0-14"),
			new ErrMsg(PI_NOT_INITIALISED  , "function called before gpioInitialise"),
			new ErrMsg(PI_INITIALISED      , "function called after gpioInitialise"),
			new ErrMsg(PI_BAD_WAVE_MODE    , "waveform mode not 0-1"),
			new ErrMsg(PI_BAD_CFG_INTERNAL , "bad parameter in gpioCfgInternals call"),
			new ErrMsg(PI_BAD_WAVE_BAUD     , "baud rate not 50-250000(RX)/1000000(TX)"),
			new ErrMsg(PI_TOO_MANY_PULSES   , "waveform has too many pulses"),
			new ErrMsg(PI_TOO_MANY_CHARS    , "waveform has too many chars"),
			new ErrMsg(PI_NOT_SERIAL_GPIO   , "no bit bang serial read in progress on GPIO"),
			new ErrMsg(PI_NOT_PERMITTED     , "no permission to update GPIO"),
			new ErrMsg(PI_SOME_PERMITTED    , "no permission to update one or more GPIO"),
			new ErrMsg(PI_BAD_WVSC_COMMND   , "bad WVSC subcommand"),
			new ErrMsg(PI_BAD_WVSM_COMMND   , "bad WVSM subcommand"),
			new ErrMsg(PI_BAD_WVSP_COMMND   , "bad WVSP subcommand"),
			new ErrMsg(PI_BAD_PULSELEN      , "trigger pulse length not 1-100"),
			new ErrMsg(PI_BAD_SCRIPT        , "invalid script"),
			new ErrMsg(PI_BAD_SCRIPT_ID     , "unknown script id"),
			new ErrMsg(PI_BAD_SER_OFFSET    , "add serial data offset > 30 minute"),
			new ErrMsg(PI_GPIO_IN_USE       , "GPIO already in use"),
			new ErrMsg(PI_BAD_SERIAL_COUNT  , "must read at least a byte at a time"),
			new ErrMsg(PI_BAD_PARAM_NUM     , "script parameter id not 0-9"),
			new ErrMsg(PI_DUP_TAG           , "script has duplicate tag"),
			new ErrMsg(PI_TOO_MANY_TAGS     , "script has too many tags"),
			new ErrMsg(PI_BAD_SCRIPT_CMD    , "illegal script command"),
			new ErrMsg(PI_BAD_VAR_NUM       , "script variable id not 0-149"),
			new ErrMsg(PI_NO_SCRIPT_ROOM    , "no more room for scripts"),
			new ErrMsg(PI_NO_MEMORY         , "can't allocate temporary memory"),
			new ErrMsg(PI_SOCK_READ_FAILED  , "socket read failed"),
			new ErrMsg(PI_SOCK_WRIT_FAILED  , "socket write failed"),
			new ErrMsg(PI_TOO_MANY_PARAM    , "too many script parameters (> 10)"),
			new ErrMsg(PI_NOT_HALTED        , "script already running or failed"),
			new ErrMsg(PI_BAD_TAG           , "script has unresolved tag"),
			new ErrMsg(PI_BAD_MICS_DELAY    , "bad MICS delay (too large)"),
			new ErrMsg(PI_BAD_MILS_DELAY    , "bad MILS delay (too large)"),
			new ErrMsg(PI_BAD_WAVE_ID       , "non existent wave id"),
			new ErrMsg(PI_TOO_MANY_CBS      , "No more CBs for waveform"),
			new ErrMsg(PI_TOO_MANY_OOL      , "No more OOL for waveform"),
			new ErrMsg(PI_EMPTY_WAVEFORM    , "attempt to create an empty waveform"),
			new ErrMsg(PI_NO_WAVEFORM_ID    , "No more waveform ids"),
			new ErrMsg(PI_I2C_OPEN_FAILED   , "can't open I2C device"),
			new ErrMsg(PI_SER_OPEN_FAILED   , "can't open serial device"),
			new ErrMsg(PI_SPI_OPEN_FAILED   , "can't open SPI device"),
			new ErrMsg(PI_BAD_I2C_BUS       , "bad I2C bus"),
			new ErrMsg(PI_BAD_I2C_ADDR      , "bad I2C address"),
			new ErrMsg(PI_BAD_SPI_CHANNEL   , "bad SPI channel"),
			new ErrMsg(PI_BAD_FLAGS         , "bad i2c/spi/ser open flags"),
			new ErrMsg(PI_BAD_SPI_SPEED     , "bad SPI speed"),
			new ErrMsg(PI_BAD_SER_DEVICE    , "bad serial device name"),
			new ErrMsg(PI_BAD_SER_SPEED     , "bad serial baud rate"),
			new ErrMsg(PI_BAD_PARAM         , "bad i2c/spi/ser parameter"),
			new ErrMsg(PI_I2C_WRITE_FAILED  , "I2C write failed"),
			new ErrMsg(PI_I2C_READ_FAILED   , "I2C read failed"),
			new ErrMsg(PI_BAD_SPI_COUNT     , "bad SPI count"),
			new ErrMsg(PI_SER_WRITE_FAILED  , "ser write failed"),
			new ErrMsg(PI_SER_READ_FAILED   , "ser read failed"),
			new ErrMsg(PI_SER_READ_NO_DATA  , "ser read no data available"),
			new ErrMsg(PI_UNKNOWN_COMMAND   , "unknown command"),
			new ErrMsg(PI_SPI_XFER_FAILED   , "SPI xfer/read/write failed"),
			new ErrMsg(PI_BAD_POINTER      , "bad (NULL) pointer"),
			new ErrMsg(PI_NO_AUX_SPI        , "no auxiliary SPI on Pi A or B"),
			new ErrMsg(PI_NOT_PWM_GPIO      , "GPIO is not in use for PWM"),
			new ErrMsg(PI_NOT_SERVO_GPIO    , "GPIO is not in use for servo pulses"),
			new ErrMsg(PI_NOT_HCLK_GPIO     , "GPIO has no hardware clock"),
			new ErrMsg(PI_NOT_HPWM_GPIO     , "GPIO has no hardware PWM"),
			new ErrMsg(PI_BAD_HPWM_FREQ     , "hardware PWM frequency not 1-125M"),
			new ErrMsg(PI_BAD_HPWM_DUTY     , "hardware PWM dutycycle not 0-1M"),
			new ErrMsg(PI_BAD_HCLK_FREQ     , "hardware clock frequency not 4689-250M"),
			new ErrMsg(PI_BAD_HCLK_PASS     , "need password to use hardware clock 1"),
			new ErrMsg(PI_HPWM_ILLEGAL      , "illegal, PWM in use for main clock"),
			new ErrMsg(PI_BAD_DATABITS      , "serial data bits not 1-32"),
			new ErrMsg(PI_BAD_STOPBITS      , "serial (half) stop bits not 2-8"),
			new ErrMsg(PI_MSG_TOOBIG        , "socket/pipe message too big"),
			new ErrMsg(PI_BAD_MALLOC_MODE   , "bad memory allocation mode"),
			new ErrMsg(PI_TOO_MANY_SEGS    , "too many I2C transaction segments"),
			new ErrMsg(PI_BAD_I2C_SEG      , "an I2C transaction segment failed"),
			new ErrMsg(PI_BAD_SMBUS_CMD     , "SMBus command not supported"),
			new ErrMsg(PI_NOT_I2C_GPIO      , "no bit bang I2C in progress on GPIO"),
			new ErrMsg(PI_BAD_I2C_WLEN      , "bad I2C write length"),
			new ErrMsg(PI_BAD_I2C_RLEN      , "bad I2C read length"),
			new ErrMsg(PI_BAD_I2C_CMD       , "bad I2C command"),
			new ErrMsg(PI_BAD_I2C_BAUD      , "bad I2C baud rate, not 50-500k"),
			new ErrMsg(PI_CHAIN_LOOP_CNT    , "bad chain loop count"),
			new ErrMsg(PI_BAD_CHAIN_LOOP    , "empty chain loop"),
			new ErrMsg(PI_CHAIN_COUNTER     , "too many chain counters"),
			new ErrMsg(PI_BAD_CHAIN_CMD     , "bad chain command"),
			new ErrMsg(PI_BAD_CHAIN_DELAY   , "bad chain delay micros"),
			new ErrMsg(PI_CHAIN_NESTING     , "chain counters nested too deeply"),
			new ErrMsg(PI_CHAIN_TOO_BIG     , "chain is too long"),
			new ErrMsg(PI_DEPRECATED        , "deprecated function removed"),
			new ErrMsg(PI_BAD_SER_INVERT    , "bit bang serial invert not 0 or 1"),
			new ErrMsg(PI_BAD_EDGE         , "bad ISR edge value, not 0-2"),
			new ErrMsg(PI_BAD_ISR_INIT     , "bad ISR initialisation"),
			new ErrMsg(PI_BAD_FOREVER       , "loop forever must be last chain command"),
			new ErrMsg(PI_BAD_FILTER        , "bad filter parameter"),

			};

	String errorText(int code){
		String text = "";
		for(ErrMsg e:errors)
			if (e.code == code)
				text = e.msg;
		return text;

	}
	
} // End of class
// End of file