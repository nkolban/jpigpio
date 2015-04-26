package jpigpio;

/**
 * The exposed pigpio functions as Java methods.
 *
 */
public interface JPigpio {
		
	public void gpioInitialize() throws PigpioException;

	public void gpioTerminate() throws PigpioException;

	public void gpioSetMode(int pin, int mode) throws PigpioException;

	public int gpioGetMode(int pin) throws PigpioException;

	public void gpioSetPullUpDown(int pin, int pud) throws PigpioException;

	public int gpioRead(int pin) throws PigpioException;

	public void gpioWrite(int pin, int value) throws PigpioException;
	
	public void gpioServo(int pin, int pulseWidth) throws PigpioException;

	public void gpioDelay(int delay) throws PigpioException;
	
	public long gpioTick() throws PigpioException;
	
	public int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException;
	
	public void i2cClose(int handle) throws PigpioException;
	
	public int i2cReadDevice(int handle, byte data[]) throws PigpioException;
	
	public void i2cWriteDevice(int handle, byte data[]) throws PigpioException;
	
	public void gpioSetAlertFunc(int pin, Alert alert) throws PigpioException;
	
} // End of interface
// End of file