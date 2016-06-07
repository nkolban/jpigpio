package jpigpio;

import java.util.ArrayList;

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

	// ############## NOTIFICATIONS

	/**
	 * Returns a notification handle (>=0).
	 *
	 * A notification is a method for being notified of GPIO state
	 * changes via a pipe.
	 *
	 * Pipes are only accessible from the local machine so this
	 * function serves no purpose if you are using Python from a
	 * remote machine.  The in-built (socket) notifications
	 * provided by [*callback*] should be used instead.
	 *
	 * Notifications for handle x will be available at the pipe
	 * named /dev/pigpiox (where x is the handle number).
	 *
	 * E.g. if the function returns 15 then the notifications must be
	 * read from /dev/pigpio15.
	 *
	 * Notifications have the following structure.
	 *
	 * . .
	 * I seqno
	 * I flags
	 * I tick
	 * I level
	 * . .
	 *
	 * seqno: starts at 0 each time the handle is opened and then
	 * increments by one for each report.
	 *
	 * flags: two flags are defined, PI_NTFY_FLAGS_WDOG and
	 * PI_NTFY_FLAGS_ALIVE.  If bit 5 is set (PI_NTFY_FLAGS_WDOG)
	 * then bits 0-4 of the flags indicate a GPIO which has had a
	 * watchdog timeout; if bit 6 is set (PI_NTFY_FLAGS_ALIVE) this
	 * indicates a keep alive signal on the pipe/socket and is sent
	 * once a minute in the absence of other notification activity.
	 *
	 * tick: the number of microseconds since system boot.  It wraps
	 * around after 1h12m.
	 *
	 * level: indicates the level of each GPIO.  If bit 1<<x is set
	 * then GPIO x is high.
	 *
	 * ...
	 * h = pi.notify_open()
	 * if h >= 0:
	 * pi.notify_begin(h, 1234)
	 * ...
	 * @return
	 * 	notification handle
	 * @throws PigpioException
     */
	public int notifyOpen() throws PigpioException;

	/**
	 * Starts notifications on a handle.
	 *
	 * The notification sends state changes for each GPIO whose
	 * corresponding bit in bits is set.
	 *
	 * The following code starts notifications for GPIO 1, 4,
	 * 6, 7, and 10 (1234 = 0x04D2 = 0b0000010011010010).
	 *
	 * ...
	 * h = pi.notify_open()
	 * if h >= 0:
	 * pi.notify_begin(h, 1234)
	 * ...
	 * @param handle
	 * 	>=0 (as returned by a prior call to [*notify_open*])
	 * @param bits
	 * 	a 32 bit mask indicating the GPIO to be notified.
	 * @return
	 * 	error code
	 * @throws PigpioException
     */
	public int notifyBegin(int handle, int bits) throws PigpioException;

	/**
	 * Pauses notifications on a handle.
	 *
	 * Notifications for the handle are suspended until
	 * [*notify_begin*] is called again.
	 *
	 * ...
	 * h = pi.notify_open()
	 * if h >= 0:
	 * pi.notify_begin(h, 1234)
	 * ...
	 * pi.notify_pause(h)
	 * ...
	 * pi.notify_begin(h, 1234)
	 * ...
	 * ...
	 * @param handle
	 * 	>=0 (as returned by a prior call to [*notify_open*])
	 * @return error code
	 * @throws PigpioException
     */
	public int notifyPause(int handle) throws PigpioException;

	/**
	 * Stops notifications on a handle and releases the handle for reuse.
	 *
	 * ...
	 * h = pi.notify_open()
	 * if h >= 0:
	 * pi.notify_begin(h, 1234)
	 * ...
	 * pi.notify_close(h)
	 * ...
	 * ...
	 * @param handle
	 * 	>=0 (as returned by a prior call to [*notify_open*])
	 * @throws PigpioException
     */
	public int notifyClose(int handle) throws PigpioException;

	/**
	 * Sets a watchdog timeout for a GPIO.
	 *
	 * The watchdog is nominally in milliseconds.
	 *
	 * Only one watchdog may be registered per GPIO.
	 *
	 * The watchdog may be cancelled by setting timeout to 0.
	 *
	 * If no level change has been detected for the GPIO for timeout
	 * milliseconds any notification for the GPIO has a report written
	 * to the fifo with the flags set to indicate a watchdog timeout.
	 *
	 * The callback class interprets the flags and will
	 * call registered notificationListeners for the GPIO with level TIMEOUT.
	 *
	 * ...
	 * pi.setWatchdog(23, 1000) # 1000 ms watchdog on GPIO 23
	 * pi.setWatchdog(23, 0)    # cancel watchdog on GPIO 23
	 * ...
	 * @param userGpio 0-31
	 * @param timeout 0-60000
	 * @return
	 * @throws PigpioException
     */
	public int setWatchdog(int userGpio, int timeout) throws PigpioException;

	public long gpioTick() throws PigpioException;

	public long getCurrentTick() throws PigpioException;

	// ################ WAVEFORMS
	/**
	 * This function clears all waveforms and any data added by calls to the wave_add_* functions.
	 *
	 * @return The return code from close.
	 */
	public int waveClear() throws PigpioException;

	/**
	 * Adds a list of pulses to the current waveform.
	 *
	 * pulses:= list of pulses to add to the waveform.
	 *
	 * Returns the new total number of pulses in the current waveform.
	 *
	 * The pulses are interleaved in time order within the existing
	 * waveform (if any).
	 *
	 * Merging allows the waveform to be built in parts, that is the
	 * settings for GPIO#1 can be added, and then GPIO#2 etc.
	 *
	 * If the added waveform is intended to start after or within
	 * the existing waveform then the first pulse should consist
	 * solely of a delay.
	 *
	 * ...
	 * G1=4
	 * G2=24
	 *
	 * pi.set_mode(G1, pigpio.OUTPUT)
	 * pi.set_mode(G2, pigpio.OUTPUT)
	 *
	 * flash_500=[] # flash every 500 ms
	 * flash_100=[] # flash every 100 ms
	 *
	 * #                              ON     OFF  DELAY
	 *
	 * flash_500.addListener(pigpio.pulse(1<<G1, 1<<G2, 500000))
	 * flash_500.addListener(pigpio.pulse(1<<G2, 1<<G1, 500000))
	 *
	 * flash_100.addListener(pigpio.pulse(1<<G1, 1<<G2, 100000))
	 * flash_100.addListener(pigpio.pulse(1<<G2, 1<<G1, 100000))
	 *
	 * pi.wave_clear() # clear any existing waveforms
	 *
	 * pi.wave_add_generic(flash_500) # 500 ms flashes
	 * f500 = pi.wave_create() # create and save id
	 *
	 * pi.wave_add_generic(flash_100) # 100 ms flashes
	 * f100 = pi.wave_create() # create and save id
	 *
	 * pi.wave_send_repeat(f500)
	 *
	 * time.sleep(4)
	 *
	 * pi.wave_send_repeat(f100)
	 *
	 * time.sleep(4)
	 *
	 * pi.wave_send_repeat(f500)
	 *
	 * time.sleep(4)
	 *
	 * pi.wave_tx_stop() # stop waveform
	 *
	 * pi.wave_clear() # clear all waveforms
	 * ...
 	 */
	public int waveAddGeneric(ArrayList<Pulse> pulses) throws PigpioException;

	/**
	 * Adds a waveform representing serial data to the existing
	 * waveform (if any).  The serial data starts [*offset*]
	 * microseconds from the start of the waveform.
	 *
	 * Returns the new total number of pulses in the current waveform.
	 *
	 * The serial data is formatted as one start bit, [*bb_bits*]
	 * data bits, and [*bb_stop*]/2 stop bits.
	 *
	 * It is legal to add serial data streams with different baud
	 * rates to the same waveform.
	 *
	 * The bytes required for each character depend upon [*bb_bits*].
	 *
	 * For [*bb_bits*] 1-8 there will be one byte per character.
	 * For [*bb_bits*] 9-16 there will be two bytes per character.
	 * For [*bb_bits*] 17-32 there will be four bytes per character.
	 *
	 * ...
	 * pi.wave_add_serial(4, 300, 'Hello world')
	 *
	 * pi.wave_add_serial(4, 300, b"Hello world")
	 *
	 * pi.wave_add_serial(4, 300, b'\\x23\\x01\\x00\\x45')
	 *
	 * pi.wave_add_serial(17, 38400, [23, 128, 234], 5000)
	 * ...
	 * @param gpio
	 * 	GPIO to transmit data.  You must set the GPIO mode to output.
	 * @param baud
	 * 	50-1000000 bits per second.
	 * @param data
	 * 	the bytes to write.
	 * @param offset
	 * 	number of microseconds from the start of the waveform, default 0.
	 * @param bbBits
	 * 	number of data bits, default 8.
	 * @param bbStop
	 * 	number of stop half bits, default 2.
	 * @return
	 * 	Returns the new total number of pulses in the current waveform.
     * @throws PigpioException
     */
	public int waveAddSerial(int gpio, int baud, byte[] data, int offset, int bbBits, int bbStop) throws PigpioException;

	/**
	 * Starts a new empty waveform.
	 *
	 * You would not normally need to call this function as it is
	 * automatically called after a waveform is created with the
	 * [*wave_create*] function.
	 *
	 * ...
	 * pi.wave_add_new()
	 * ...
	 *
	 * @return The return code from add new.
	 */
	public int waveAddNew() throws PigpioException;

	/**
	 * Returns 1 if a waveform is currently being transmitted,
	 * otherwise 0.
	 *
	 * ...
	 * pi.wave_send_once(0) # send first waveform
	 *
	 * while pi.wave_tx_busy(): # wait for waveform to be sent
	 * time.sleep(0.1)
	 *
	 * pi.wave_send_once(1) # send next waveform
	 * ...
	 * @return The return code from wave_tx_busy.
	 */
	public boolean waveTxBusy() throws PigpioException;

	/**
	 * Stops the transmission of the current waveform.
	 *
	 * This function is intended to stop a waveform started with
	 * wave_send_repeat.
	 *
	 * ...
	 * pi.wave_send_repeat(3)
	 *
	 * time.sleep(5)
	 *
	 * pi.wave_tx_stop()
	 * ...
	 * @return The return code from wave_tx_stop.
	 */
	public int waveTxStop() throws PigpioException;

	/**
	 * Creates a waveform from the data provided by the prior calls
	 * to the [*wave_add_**] functions.
	 *
	 * Returns a wave id (>=0) if OK,  otherwise PI_EMPTY_WAVEFORM,
	 * PI_TOO_MANY_CBS, PI_TOO_MANY_OOL, or PI_NO_WAVEFORM_ID.
	 *
	 * The data provided by the [*wave_add_**] functions is consumed by
	 * this function.
	 *
	 * As many waveforms may be created as there is space available.
	 * The wave id is passed to [*wave_send_**] to specify the waveform
	 * to transmit.
	 *
	 * Normal usage would be
	 *
	 * Step 1. [*wave_clear*] to clear all waveforms and added data.
	 *
	 * Step 2. [*wave_add_**] calls to supply the waveform data.
	 *
	 * Step 3. [*wave_create*] to create the waveform and get a unique id
	 *
	 * Repeat steps 2 and 3 as needed.
	 *
	 * Step 4. [*wave_send_**] with the id of the waveform to transmit.
	 *
	 * A waveform comprises one or more pulses.
	 *
	 * A pulse specifies
	 *
	 * 1) the GPIO to be switched on at the start of the pulse.
	 * 2) the GPIO to be switched off at the start of the pulse.
	 * 3) the delay in microseconds before the next pulse.
	 *
	 * Any or all the fields can be zero.  It doesn't make any sense
	 * to set all the fields to zero (the pulse will be ignored).
	 *
	 * When a waveform is started each pulse is executed in order with
	 * the specified delay between the pulse and the next.
	 *
	 * ...
	 * wid = pi.wave_create()
	 * ...
	 * @return
	 * 	wave id (>=0) if OK,  otherwise PI_EMPTY_WAVEFORM, PI_TOO_MANY_CBS,
	 * 	PI_TOO_MANY_OOL, or PI_NO_WAVEFORM_ID.
	 * @throws PigpioException
     */
	public int waveCreate() throws PigpioException;

	/**
	 * This function deletes the waveform with id wave_id.
	 *
	 * wave_id:= >=0 (as returned by a prior call to [*wave_create*]).
	 *
	 * Wave ids are allocated in order, 0, 1, 2, etc.
	 *
	 * ...
	 * pi.wave_delete(6) # delete waveform with id 6
	 *
	 * pi.wave_delete(0) # delete waveform with id 0
	 * ...
	 * @return The return code from wave_delete.
	 */
	public int waveDelete(int waveId) throws PigpioException;

	/**
	 * Transmits the waveform with id wave_id.  The waveform is sent once.
	 *
	 * NOTE: Any hardware PWM started by [*hardware_PWM*] will be cancelled.
	 *
	 * Returns the number of DMA control blocks used in the waveform.
	 *
	 * ...
	 * cbs = pi.wave_send_once(wid)
	 * ...
	 * @param waveId
	 *   >=0 (as returned by a prior call to [*wave_create*]).
	 * @return
	 * 	Returns the number of DMA control blocks used in the waveform.
	 * @throws PigpioException
     */
	public int waveSendOnce(int waveId) throws PigpioException;

	/**
	 * Transmits the waveform with id wave_id.  The waveform repeats
	 * until wave_tx_stop is called or another call to [*wave_send_**]
	 * is made.
	 *
	 * NOTE: Any hardware PWM started by [*hardware_PWM*] will
	 * be cancelled.
	 *
	 * Returns the number of DMA control blocks used in the waveform.
	 *
	 * ...
	 * cbs = pi.wave_send_repeat(wid)
	 * ...
	 * @param waveId
	 * 	>=0 (as returned by a prior call to [*wave_create*]).
	 * @return
	 * 	Returns the number of DMA control blocks used in the waveform.
	 * @throws PigpioException
     */
	public int waveSendRepeat(int waveId) throws PigpioException;


	// ################ I2C

	public int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException;

	public void i2cClose(int handle) throws PigpioException;

	public int i2cReadDevice(int handle, byte data[]) throws PigpioException;

	public void i2cWriteDevice(int handle, byte data[]) throws PigpioException;

	// ################ SPI

	/**
	 * Returns a handle for the SPI device on channel.  Data will be
     * transferred at baud bits per second.  The flags may be used to
     * modify the default behaviour of 4-wire operation, mode 0,
     * active low chip select.
	 *
     * An auxiliary SPI device is available on all models but the
     * A and B and may be selected by setting the A bit in the
     * flags. The auxiliary device has 3 chip selects and a
     * selectable word size in bits.
	 *
     * spi_channel:= 0-1 (0-2 for the auxiliary SPI device).
     *        baud:= 32K-125M (values above 30M are unlikely to work).
     *   spi_flags:= see below.
	 *
     * Normally you would only use the [*spi_**] functions if
     * you are or will be connecting to the Pi over a network.  If
     * you will always run on the local Pi use the standard SPI
     * module instead.
	 *
     * spi_flags consists of the least significant 22 bits.
	 *
     * . .
     * 21 20 19 18 17 16 15 14 13 12 11 10  9  8  7  6  5  4  3  2  1  0
     *  b  b  b  b  b  b  R  T  n  n  n  n  W  A u2 u1 u0 p2 p1 p0  m  m
     * . .
	 *
     * mm defines the SPI mode.
	 *
     * WARNING: modes 1 and 3 do not appear to work on the auxiliary device.
	 *
     * . .
     * Mode POL PHA
     *  0    0   0
     *  1    0   1
     *  2    1   0
     *  3    1   1
     * . .
	 *
     * px is 0 if CEx is active low (default) and 1 for active high.
	 *
     * ux is 0 if the CEx GPIO is reserved for SPI (default)
     * and 1 otherwise.
	 *
     * A is 0 for the standard SPI device, 1 for the auxiliary SPI.
	 *
     * W is 0 if the device is not 3-wire, 1 if the device is 3-wire.
     * Standard SPI device only.
	 *
     * nnnn defines the number of bytes (0-15) to write before
     * switching the MOSI line to MISO to read data.  This field
     * is ignored if W is not set.  Standard SPI device only.
	 *
     * T is 1 if the least significant bit is transmitted on MOSI
     * first, the default (0) shifts the most significant bit out
     * first.  Auxiliary SPI device only.
	 *
     * R is 1 if the least significant bit is received on MISO
     * first, the default (0) receives the most significant bit
     * first.  Auxiliary SPI device only.
	 *
     * bbbbbb defines the word size in bits (0-32).  The default (0)
     * sets 8 bits per word.  Auxiliary SPI device only.
	 *
     * The [*spi_read*], [*spi_write*], and [*spi_xfer*] functions
     * transfer data packed into 1, 2, or 4 bytes according to
     * the word size in bits.
	 *
     * For bits 1-8 there will be one byte per character.
     * For bits 9-16 there will be two bytes per character.
     * For bits 17-32 there will be four bytes per character.
	 *
     * E.g. 32 12-bit words will be transferred in 64 bytes.
	 *
     * The other bits in flags should be set to zero.
	 *
     * ...
     * # open SPI device on channel 1 in mode 3 at 50000 bits per second
	 *
     * h = pi.spi_open(1, 50000, 3)
     * ...
     *
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
	 * Closes the SPI device associated with handle.
	 *
	 * handle:= >=0 (as returned by a prior call to [*spi_open*]).
     *
	 * ...
	 * pi.spi_close(h)
	 * ...
	 *
	 * @param handle The handle to be closed.
	 * @throws PigpioException
	 */
	public void spiClose(int handle) throws PigpioException;

	/**
	 * Reads count bytes from the SPI device associated with handle.
	 *
	 * handle:= >=0 (as returned by a prior call to [*spi_open*]).
	 * count:= >0, the number of bytes to read.
	 *
	 * The returned value is a tuple of the number of bytes read and a
	 * bytearray containing the bytes.  If there was an error the
	 * number of bytes read will be less than zero (and will contain
	 * the error code).
	 *
	 * ...
	 * (b, d) = pi.spi_read(h, 60) # read 60 bytes from device h
	 * if b == 60:
	 * # process read data
	 * else:
	 * # error path
	 * ...
	 * @param handle The handle from which to read.
	 * @param data An array into which to read data.
	 * @return The number of bytes actually read.
	 * @throws PigpioException
	 */
	public int spiRead(int handle, byte data[]) throws PigpioException;
	
	/**
	 * Writes the data bytes to the SPI device associated with handle.
     *
	 * handle:= >=0 (as returned by a prior call to [*spi_open*]).
	 * data:= the bytes to write.
	 *
	 * ...
	 * pi.spi_write(0, b'\\x02\\xc0\\x80') # write 3 bytes to device 0
	 *
	 * pi.spi_write(0, b'defgh')        # write 5 bytes to device 0
	 *
	 * pi.spi_write(0, "def")           # write 3 bytes to device 0
	 *
	 * pi.spi_write(1, [2, 192, 128])   # write 3 bytes to device 1
	 * ...
	 * @param handle The handle into which to write.
	 * @param data An array of data to write to SPI.
	 * @return The number of bytes actually written
	 * @throws PigpioException
	 */
	public int spiWrite(int handle, byte data[]) throws PigpioException;

	/**
	 * Writes the data bytes to the SPI device associated with handle,
	 * returning the data bytes read from the device.
	 *
	 * handle:= >=0 (as returned by a prior call to [*spi_open*]).
	 * data:= the bytes to write.
	 *
	 * The returned value is a tuple of the number of bytes read and a
	 * bytearray containing the bytes.  If there was an error the
	 * number of bytes read will be less than zero (and will contain
	 * the error code).
	 *
	 * ...
	 * (count, rx_data) = pi.spi_xfer(h, b'\\x01\\x80\\x00')
	 *
	 * (count, rx_data) = pi.spi_xfer(h, [1, 128, 0])
	 *
	 * (count, rx_data) = pi.spi_xfer(h, b"hello")
	 *
	 * (count, rx_data) = pi.spi_xfer(h, "hello")
	 * ...
	 * @param handle The handle into which to write.
	 * @param txData An array of data to write.
	 * @param rxData An array of data to read.
	 * @return The number of bytes actually transferred.
	 * @throws PigpioException
	 */
	public int spiXfer(int handle, byte txData[], byte rxData[]) throws PigpioException;

	// ################ SERIAL

	// ################ PWM

	/**
	 * Starts (non-zero dutycycle) or stops (0) PWM pulses on the GPIO.
     *
	 * The [*setPWMRange*] function can change the default range of 255.
	 *
	 * ...
	 * pi.setPWMDutycycle(4,   0) # PWM off
	 * pi.setPWMDutycycle(4,  64) # PWM 1/4 on
	 * pi.setPWMDutycycle(4, 128) # PWM 1/2 on
	 * pi.setPWMDutycycle(4, 192) # PWM 3/4 on
	 * pi.setPWMDutycycle(4, 255) # PWM full on
	 * ...
	 *
	 * @param gpio user gpio 0-31
	 * @param dutycycle 0-range (range defaults to 255).
	 * @throws PigpioException
     */
	public void setPWMDutycycle(int gpio, int dutycycle) throws PigpioException;

	/**
	 * Returns the PWM dutycycle being used on the GPIO.
     *
	 * For normal PWM the dutycycle will be out of the defined range
	 * for the GPIO (see [*getPWMRange*]).
	 *
	 * If a hardware clock is active on the GPIO the reported
	 * dutycycle will be 500000 (500k) out of 1000000 (1M).
	 *
	 * If hardware PWM is active on the GPIO the reported dutycycle
	 * will be out of a 1000000 (1M).
	 *
	 * ...
	 * pi.setPWMDutycycle(4, 25)
	 * print(pi.getPWMDutycycle(4))
	 * 25
	 *
	 * pi.setPWMDutycycle(4, 203)
	 * print(pi.getPWMDutycycle(4))
	 * 203
	 * ...
	 * @param gpio user gpio 0-31
	 * @return PWM dutycycle
	 * @throws PigpioException
     */
	public int getPWMDutycycle(int gpio) throws PigpioException;


	/**
	 * Sets the range of PWM values to be used on the GPIO.
     *
	 * ...
	 * pi.set_PWM_range(9, 100)  # now  25 1/4,   50 1/2,   75 3/4 on
	 * pi.set_PWM_range(9, 500)  # now 125 1/4,  250 1/2,  375 3/4 on
	 * pi.set_PWM_range(9, 3000) # now 750 1/4, 1500 1/2, 2250 3/4 on
	 * ...
	 *
	 * @param gpio user gpio 0-31
	 * @param range range 25-40000
	 * @throws PigpioException
     */
	public void setPWMRange(int gpio, int range) throws PigpioException;

	/**
	 * Returns the range of PWM values being used on the GPIO.
     *
	 * If a hardware clock or hardware PWM is active on the GPIO
	 * the reported range will be 1000000 (1M).
	 *
	 * ...
	 * pi.set_PWM_range(9, 500)
	 * print(pi.get_PWM_range(9))
	 * 500
	 * ...
	 * @param gpio user gpio 0-31
	 * @return range of PWM values
	 * @throws PigpioException
     */
	public int getPWMRange(int gpio) throws PigpioException;

	/**
	 * Returns the real (underlying) range of PWM values being
	 * used on the GPIO.
	 *
	 * If a hardware clock is active on the GPIO the reported
	 * real range will be 1000000 (1M).
	 *
	 * If hardware PWM is active on the GPIO the reported real range
	 * will be approximately 250M divided by the set PWM frequency.
	 *
	 * ...
	 * pi.setPWMFrequency(4, 800)
	 * System.out.println(pi.getPWMRealRange(4))
	 * 250
	 * ...
	 * @param gpio user gpio 0-31
	 * @return the real (underlying) range of PWM values
	 * @throws PigpioException
     */
	public int getPWMRealRange(int gpio) throws PigpioException;

	/**
	 * Sets the frequency (in Hz) of the PWM to be used on the GPIO.
     *
	 * Returns the frequency actually set.
	 *
	 * ...
	 * pi.setPWMFrequency(4,0)
	 * print(pi.getPWMFrequency(4))
	 * 10
	 *
	 * pi.set_PWMFrequency(4,100000)
	 * print(pi.getPWMFrequency(4))
	 * 8000
	 * ...
	 * @param gpio user gpio 0-31
	 * @param frequency >= 0 Hz
	 * @throws PigpioException
     */
	public int setPWMFrequency(int gpio, int frequency) throws PigpioException;

	/**
	 * Returns the frequency of PWM being used on the GPIO.
     *
	 * Returns the frequency (in Hz) used for the GPIO.
	 *
	 * For normal PWM the frequency will be that defined for the GPIO
	 * by [*setPWMFrequency*].
	 *
	 * If a hardware clock is active on the GPIO the reported frequency
	 * will be that set by [*hardwareClock*].
	 *
	 * If hardware PWM is active on the GPIO the reported frequency
	 * will be that set by [*hardwarePWM*].
	 *
	 * ...
	 * pi.setPWMFrequency(4,0)
	 * print(pi.getPWMFrequency(4))
	 * 10
	 *
	 * pi.setPWMFrequency(4, 800)
	 * print(pi.getPWMFrequency(4))
	 * 800
	 * ...
	 * @param gpio user gpio 0-31
	 * @return frequency (in Hz) used for the GPIO.
	 * @throws PigpioException
     */
	public int getPWMFrequency(int gpio) throws PigpioException;

	// ################

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

	/**
	 * Add callback listener object which would receive notifications related to
	 * GPIO levels which listener has specified.
	 * @param listener
	 * 	GPIOListener object
	 * @throws PigpioException
     */
	public void addCallback(GPIOListener listener) throws PigpioException;

	/**
	 * Remove callback listener object from notification thread.
	 * @param listener
	 * 	GPIOListener object
	 * @throws PigpioException
     */
	public void removeCallback(GPIOListener listener) throws PigpioException;

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

	/* edges: 0-2 */

	public static final int PI_RISING_EDGE = 0;
	public static final int PI_FALLING_EDGE = 1;
	public static final int PI_EITHER_EDGE = 2;

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

	// script run status

	public static final int PI_SCRIPT_INITING = 0;
	public static final int PI_SCRIPT_HALTED  = 1;
	public static final int PI_SCRIPT_RUNNING = 2;
	public static final int PI_SCRIPT_WAITING = 3;
	public static final int PI_SCRIPT_FAILED  = 4;

	// notification flags

	public static final int PI_NTFY_FLAGS_ALIVE = (1 << 6);
	public static final int PI_NTFY_FLAGS_WDOG  = (1 << 5);
	public static final int PI_NTFY_FLAGS_GPIO  = 31;

	// wave modes

	public static final int PI_WAVE_MODE_ONE_SHOT     	=0;
	public static final int PI_WAVE_MODE_REPEAT       	=1;
	public static final int PI_WAVE_MODE_ONE_SHOT_SYNC	=2;
	public static final int PI_WAVE_MODE_REPEAT_SYNC  	=3;

	public static final int PI_WAVE_NOT_FOUND = 9998; // Transmitted wave not found.
	public static final int PI_NO_TX_WAVE     = 9999; // No wave being transmitted.


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