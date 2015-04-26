package jpigpio;

public class Pigpio implements JPigpio {
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
	public native void gpioWrite(int pin, int value) throws PigpioException;

	@Override
	public native int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException;
	
	@Override
	public native void i2cClose(int handle) throws PigpioException;
	
	@Override
	public native int i2cReadDevice(int handle, byte[] data) throws PigpioException;

	@Override
	public native void i2cWriteDevice(int handle, byte[] data) throws PigpioException;
	
	@Override
	public native void gpioDelay(int delay) throws PigpioException;

	@Override
	public native long gpioTick() throws PigpioException;

	@Override
	public native void gpioServo(int pin, int pulseWidth) throws PigpioException;

	@Override
	public native void gpioSetAlertFunc(int pin, Alert alert) throws PigpioException;
} // End of class
// End of file