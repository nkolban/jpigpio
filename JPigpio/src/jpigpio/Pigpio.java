package jpigpio;

import jpigpio.impl.CommonPigpio;

import java.util.ArrayList;

/**
 * Pigpiod wrapper class using native C++ methods. This class can be used only when executing your application directly at Raspberry Pi device.
 * See {@link PigpioSocket class PigpioSocket} for socket based implementation allowing you to develop/debug/run your over network.
 * <br><br>
 * See {@link JPigpio interface JPigpio} for full documentation of specific methods.<br>
 * <br>
 * Methods which are not implemented yet has "Not implemented" in their description.<br>
 * All other methods you can consider implemented. <br>
 * <br>
 * NOTE: if method you are looking for is not implemented, check {@link PigpioSocket class PigpioSocket} - maybe it is implemented there.
 */
public class Pigpio extends CommonPigpio {
	static {
		System.loadLibrary("JPigpioC");
	}

	@Override
	public native void gpioInitialize() throws PigpioException;

	@Override
	public native void gpioTerminate() throws PigpioException;

	/**
	 * Not implemented
	 */
	@Override
	public void reconnect() throws PigpioException{
		// do nothing for native interface
		return;
	}

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

	/**
	 * Not implemented
	 */
	@Override
	public int notifyOpen() throws PigpioException{
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void notifyBegin(int handle, int bits) throws PigpioException{
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void notifyPause(int handle) throws PigpioException{
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void notifyClose(int handle) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void setWatchdog(int userGpio, int timeout) throws PigpioException{
		throw new NotImplementedException();
	}

	// ##################### WAVEFORMS

	/**
	 * Not implemented
	 */
	@Override
	public void waveClear() throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int waveAddGeneric(ArrayList<Pulse> pulses) throws PigpioException{
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int waveAddSerial(int userGpio, int baud, byte[] data, int offset, int bbBits, int bbStop) throws PigpioException{
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void waveAddNew() throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public boolean waveTxBusy() throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int waveTxStop() throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int waveCreate() throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void waveDelete(int waveId) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int waveSendOnce(int waveId) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int waveSendRepeat(int waveId) throws PigpioException {
		throw new NotImplementedException();
	}


	// ################ I2C

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


	@Override
	public native void gpioServo(int gpio, int pulseWidth) throws PigpioException;

	@Override
	public void setServoPulseWidth(int gpio, int pulseWidth) throws PigpioException {
		gpioServo(gpio, pulseWidth);
	}

	/**
	 * Not implemented
	 */
	@Override
	public int getServoPulseWidth(int gpio) throws PigpioException{
		throw new NotImplementedException();
	}

	// ############### PWM

	@Override
	public void setPWMDutycycle(int gpio, int dutycycle) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int getPWMDutycycle(int gpio) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void setPWMRange(int gpio, int range) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int getPWMRange(int gpio) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int getPWMRealRange(int gpio) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int setPWMFrequency(int gpio, int frequency) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public int getPWMFrequency(int gpio) throws PigpioException {
		throw new NotImplementedException();
	}

	// ################ SERIAL
	/**
	 * Not implemented
	 */
	@Override
	public int serialOpen(String tty, int baudRate, int flags) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void serialClose(int handle) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public byte serialReadByte(int handle) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void serialWriteByte(int handle, byte data) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public byte[] serialRead(int handle, int count) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
	 */
	@Override
	public void serialWrite(int handle, byte[] data) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
     */
	@Override
	public int serialDataAvailable(int handle) throws PigpioException {
		throw new NotImplementedException();
	}

	// ###############


	@Override
	public native void gpioSetAlertFunc(int pin, Alert alert) throws PigpioException;

	@Override
	public native void gpioTrigger(int gpio, long pulseLen, boolean level) throws PigpioException;

	@Override
	public native int spiOpen(int channel, int baudRate, int flags) throws PigpioException;

	@Override
	public native void spiClose(int handle) throws PigpioException;

	@Override
	public native int spiRead(int handle, byte[] data) throws PigpioException;

	@Override
	public native int spiWrite(int handle, byte[] data) throws PigpioException;

	@Override
	public native int spiXfer(int handle, byte[] txData, byte[] rxData) throws PigpioException;

	@Override
	/**
	 * Set whether or not debugging is enabled. True is enabled.
	 */
	public native void setDebug(boolean flag);


	@Override
	public native long gpioxPulseAndWait(int outGpio, int inGpio, long waitDuration, long pulseHoldDuration, boolean pulseLow) throws PigpioException;

	/**
	 * Not implemented
     */
	@Override
	public void addCallback(GPIOListener gpioListener) throws PigpioException {
		throw new NotImplementedException();
	}

	/**
	 * Not implemented
     */
	@Override
	public void removeCallback(GPIOListener cgpioListener) throws PigpioException{
		throw new NotImplementedException();
	}

} // End of class
// End of file