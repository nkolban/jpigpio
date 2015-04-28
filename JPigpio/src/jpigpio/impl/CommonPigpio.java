package jpigpio.impl;

import jpigpio.JPigpio;
import jpigpio.PigpioException;

public abstract class CommonPigpio implements JPigpio {

	@Override
	public void gpioShiftOut(int gpioData, int gpioClock, boolean bitOrder, int value) throws PigpioException {
		
		boolean bit;
		for (int i=0; i< 8; i++) {
			if (bitOrder == PI_LSBFIRST) {
				bit = ((value & 0x01) != 0);
				value = (byte)(value >> 1);
			} else {
				bit = ((value & 0x80) != 0);
				value = (byte)(value << 1);
			}
			gpioWrite(gpioData, bit);
			gpioTrigger(gpioClock, 10, PI_HIGH);
		} // End of each bit
	} // End of gpioShiftOut
} // End of class
// End of file