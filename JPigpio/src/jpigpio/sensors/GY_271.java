package jpigpio.sensors;

import jpigpio.JPigpio;
import jpigpio.PigpioException;

/**
 * The GY-271 is a module that provides a multi axis magnetic field measurement that is typically used to provide a compass. It is based on the Honeywell HMC5883L IC. The module
 * uses I2C for communications.
 * 
 * This Java class provides a wrapper to its low level functions for use with Pigpio.
 * 
 * author Neil Kolban
 * date 2015-05-06
 */
public class GY_271 {

	
	// REGISTER_CONFIG_A (0)
	// +----+-----+------------------------------------+
	// |    | 7   | Reserved.  Must be 0               |
	// +----+-----+------------------------------------+
	// | MA | 6:5 | Samples per measurement            |
	// +----+-----+------------------------------------+
	// | DO | 4:2 | Output rate in measurements/second |
	// +----+-----+------------------------------------+
	// | MS | 1:0 | Measurement configuration          |
	// +----+-----+------------------------------------+
	public static final int SAMPLES_1 = 0b00;
	public static final int SAMPLES_2 = 0b01;
	public static final int SAMPLES_4 = 0b10;
	public static final int SAMPLES_8 = 0b11;
	private static final int SAMPLE_MASK = 0b1100000;
	
	public static final int OUTPUT_RATE_0_75 = 0b000;
	public static final int OUTPUT_RATE_1_5 = 0b001;
	public static final int OUTPUT_RATE_3 = 0b010;
	public static final int OUTPUT_RATE_7_5 = 0b011;	
	public static final int OUTPUT_RATE_15 = 0b100;	
	public static final int OUTPUT_RATE_30 = 0b101;	
	public static final int OUTPUT_RATE_75 = 0b110;
	private static final int OUTPUT_RATE_MASK = 0b11100;
	
	// REGISTER_CONFIG_B (1)
	// +----+-----+----------------------+
	// | GN | 7:5 | Gain                 |
	// +----+-----+----------------------+
	// | MA | 4:0 | Reserved.  Must be 0 |
	// +----+-----+----------------------+
	public static final int GAIN_1370 = 0b000;
	public static final int GAIN_1090 = 0b001;
	public static final int GAIN_0820 = 0b010;
	public static final int GAIN_0660 = 0b011;
	public static final int GAIN_0440 = 0b100;
	public static final int GAIN_0390 = 0b101;
	public static final int GAIN_0330 = 0b110;
	public static final int GAIN_0230 = 0b111;
	private static final int GAIN_MASK = 0b11100000;
	
	// REGISTER_MODE (2)
	// +----+-----+----------------------+
	// | HS | 7   | High speed I2C       |
	// +----+-----+----------------------+
	// |    | 6:2 | Reserved.  Must be 0 |
	// +----+-----+----------------------+
	// | MD | 1:0 | Operating mode       |
	// +----+-----+----------------------+
	public static final int MODE_CONTINUOUS = 0b00;
	public static final int MODE_SINGLE = 0b01;
	public static final int MODE_IDLE = 0b10;
	private static final int MODE_MASK = 0b11;

	
	// These are the different registers in the environment
	private static final int REGISTER_CONFIG_A = 0;
	private static final int REGISTER_CONFIG_B = 1;
	private static final int REGISTER_MODE = 2;
	private static final int REGISTER_DATA_X_MSB = 3;
	private static final int REGISTER_DATA_X_LSB = 4;
	private static final int REGISTER_DATA_Z_MSB = 5;
	private static final int REGISTER_DATA_Z_LSB = 6;
	private static final int REGISTER_DATA_Y_MSB = 7;
	private static final int REGISTER_DATA_Y_LSB = 8;
	private static final int REGISTER_STATUS = 9;
	private static final int REGISTER_IDENT_A = 10;
	private static final int REGISTER_IDENT_B = 11;
	private static final int REGISTER_IDENT_C = 12;
	
	/**
	 * The address of the GY-271 on the I2C bus
	 */
	private final int GY_271_I2C_ADDRESS = 0x1E; // GY-271 I2C address
	/**
	 * The I2C bus we use on the Pi
	 */
	private final int PI_I2CBUS = 1;

	private int xValue = 0;
	private int yValue = 0;
	private int zValue = 0;

	/**
	 * The Pigpio interface
	 */
	private JPigpio pigpio;
	/**
	 * The handle to the I2C device
	 */
	private int handle;

	/**
	 * Construct the object for this class.
	 * @param pigpio The reference to the Pigpio controller
	 */
	public GY_271(JPigpio pigpio) {
		this.pigpio = pigpio;
	} // End of constructor

	/**
	 * Initialize the GY-271
	 * 
	 * @throws PigpioException on pigpiod error
	 */
	public void initialize() throws PigpioException {
		// Open a connection on I2C to the GY-271 device.
		handle = pigpio.i2cOpen(PI_I2CBUS, GY_271_I2C_ADDRESS);
		
		// Select register 2 and set for continuous measurement
		setMode(MODE_CONTINUOUS);
		setSamples(SAMPLES_1);
		setOutputRate(15);
		setGain(GAIN_1090);

		pigpio.gpioDelay(500);
	} // End of initialize

	/**
	 * Close our usage of the I2C bus.
	 * 
	 * @throws PigpioException on pigpiod error
	 */
	public void close() throws PigpioException {
		pigpio.i2cClose(handle);
	} // End of close

	public void readValue() throws PigpioException {
		byte selectRegister[] = { REGISTER_DATA_X_MSB };
		pigpio.i2cWriteDevice(handle, selectRegister);
		pigpio.gpioDelay(100, JPigpio.PI_MILLISECONDS);
		
		// Read 6 bytes of data corresponding to the 3 pairs of bytes
		byte data[] = new byte[6];
		pigpio.i2cReadDevice(handle, data);
		xValue = data[0] << 8 | data[1];
		zValue = data[2] << 8 | data[3];
		yValue = data[4] << 8 | data[4];
//		byte data[] = new byte[1];
//		pigpio.i2cReadDevice(handle, data);
//		xValue = data[0] << 8;
//		pigpio.i2cReadDevice(handle, data);
//		xValue |= data[0];
//		pigpio.i2cReadDevice(handle, data);
//		zValue = data[0] << 8;
//		pigpio.i2cReadDevice(handle, data);
//		zValue |= data[0];
//		pigpio.i2cReadDevice(handle, data);
//		yValue = data[0] << 8;
//		pigpio.i2cReadDevice(handle, data);
//		yValue |= data[0];

	} // End of readValue

	public int getX() {
		return xValue;
	} // End of getX

	public int getY() {
		return yValue;
	} // End of getY

	public int getZ() {
		return zValue;
	} // End of getZ

	@Override
	public String toString() {
		return String.format("x: %d, y: %d, z: %d", getX(), getY(), getZ());
	} // End of toString

	/**
	 * Get the device id. A normal device will have an id of "H43".
	 * 
	 * @return The string representation of the device
	 * @throws PigpioException on pigpiod error
	 */
	public String getId() throws PigpioException {
		byte selectRegister[] = { REGISTER_IDENT_A };
		pigpio.i2cWriteDevice(handle, selectRegister);
		byte data[] = new byte[3];
		pigpio.i2cReadDevice(handle, data);
		return new String(data);
	} // End of getId

	public void setGain(int value) throws PigpioException {
		writeRegister(REGISTER_CONFIG_B, (byte)(readRegister(REGISTER_CONFIG_B) & ~GAIN_MASK | (value << 5)));
	} // End of setGain
	
	public void setMode(int value) throws PigpioException {
		writeRegister(REGISTER_MODE, (byte)(readRegister(REGISTER_MODE) & ~MODE_MASK | value ));
	} // End of setMode
	
	public void setSamples(int value) throws PigpioException {
		writeRegister(REGISTER_CONFIG_A, (byte)(readRegister(REGISTER_CONFIG_A) & ~SAMPLE_MASK | (value << 5)));
	} // End of setSamples
	
	public void setOutputRate(int value) throws PigpioException {
		writeRegister(REGISTER_CONFIG_A, (byte)(readRegister(REGISTER_CONFIG_A) & ~OUTPUT_RATE_MASK | (value << 2)));
	} // End of setOutputRate
	
	public byte getStatus() throws PigpioException {
		return readRegister(REGISTER_STATUS);
	} // End of getStatus
	
	private byte readRegister(int register) throws PigpioException {
		byte data[] = { (byte) register };
		pigpio.i2cWriteDevice(handle, data);	
		pigpio.i2cReadDevice(handle, data);
		return data[0];
	} // End of readRegister
	
	private void writeRegister(int register, byte value) throws PigpioException {
		byte data[] = { (byte) register, value };
		pigpio.i2cWriteDevice(handle, data);	
	} // End of write register
} // End of class
// End of file