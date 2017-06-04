package jpigpio.impl;

import jpigpio.GPIO;
import jpigpio.JPigpio;
import jpigpio.PigpioException;

public abstract class CommonPigpio implements JPigpio {

	/**
	 * Shift out a byte of data to a given pin.  Note that this function is implemented in
	 * Javacode.
	 * @param gpioData The gpio to which to write the data.
	 * @param gpioClock The clock gpio to pulse. The clock level is high.
	 * @param bitOrder The order of the bits
	 * @param value The value of the byte to write.
	 * @throws PigpioException  on pigpiod error
	 */
	@Override
	public void gpioShiftOut(int gpioData, int gpioClock, boolean bitOrder, byte value) throws PigpioException {
		gpioShiftOut(gpioData, gpioClock, true, bitOrder, value);
	}
	@Override
	public void gpioShiftOut(GPIO gpioData, GPIO gpioClock, boolean bitOrder, byte value) throws PigpioException {
		gpioShiftOut(gpioData, gpioClock, true, bitOrder, value);
	}
		
	/**
	 * Shift out a byte of data to a given pin.  Note that this function is implemented in
	 * Javacode.
	 * @param gpioData The gpio to which to write the data.
	 * @param gpioClock The clock gpio to pulse.
	 * @param clockLevel The value of the clock pulse
	 * @param bitOrder The order of the bits
	 * @param value The value of the byte to write.
	 * @throws PigpioException  on pigpiod error
	 */
	@Override
	public void gpioShiftOut(int gpioData, int gpioClock, boolean clockLevel, boolean bitOrder, byte value) throws PigpioException {

		boolean bit;
		for (int i = 0; i < 8; i++) {
			if (bitOrder == PI_LSBFIRST) {
				bit = ((value & 0x01) != 0);
				value = (byte) (value >> 1);
			} else {
				bit = ((value & 0x80) != 0);
				value = (byte) (value << 1);
			}
			gpioWrite(gpioData, bit);
			// Trigger this clock in high for 10 usecs
			gpioTrigger(gpioClock, 10, clockLevel);
		} // End of each bit
	} // End of gpioShiftOut
	
	@Override
	public void gpioShiftOut(GPIO gpioData, GPIO gpioClock, boolean clockLevel, boolean bitOrder, byte value) throws PigpioException {

		boolean bit;
		for (int i = 0; i < 8; i++) {
			if (bitOrder == PI_LSBFIRST) {
				bit = ((value & 0x01) != 0);
				value = (byte) (value >> 1);
			} else {
				bit = ((value & 0x80) != 0);
				value = (byte) (value << 1);
			}
			gpioData.setValue(bit);
			// Trigger this clock in high for 10 usecs
			gpioTrigger(gpioClock.getPin(), 10, clockLevel);
		} // End of each bit
	} // End of gpioShiftOut

	@Override
	public void gpioDelay(long delay, int type) throws PigpioException {
		switch (type) {
		case JPigpio.PI_MICROSECONDS:
			gpioDelay(delay);
			break;
		case JPigpio.PI_SECONDS:
			delay = delay * 1000;
		case JPigpio.PI_MILLISECONDS:
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		} // End of switch

	} // End of gpioDelay

} // End of class
// End of file