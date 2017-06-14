/*
Copyright (C) 2015 Neil Kolban <kolban1@kolban.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the version 3 GNU General Public License as
published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package jpigpio.devices;

import jpigpio.GPIO;
import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.WrongModeException;

/**
 * A data sheet can be found at <a href="https://docs.google.com/file/d/0B4JhBWQiFENbNnpEVTAtUFZzdHM/edit">Datasheet</a>.
 * 
 * This code is based upon work performed by Ricardo Batista in his project called "tm1638-library" found here:
 * <a href="https://code.google.com/p/tm1638-library/">https://code.google.com/p/tm1638-library/</a>.
 *
 *author Neil Kolban
 *email kolban1@kolban.com
 *date 2015-05-23
 */
public class TM1638 {
	private JPigpio pigpio;
	private GPIO stbGpio; // When strobe is high, chip is disabled.
	private GPIO clkGpio; // Clock in data on a rising edge
	private GPIO dioGpio; // Data input and output
	
	private boolean isTestMode;
	private boolean isFixedAddress;
	private boolean isReadKeys;
	private boolean isDisplayOn;
	private int brightness;
	
	private final byte DATA_COMMAND		= (byte)0b01000000;
	private final byte ADDRESS_COMMAND	= (byte)0b11000000;
	private final byte DISPLAY_COMMAND	= (byte)0b10000000;
	private final byte TEST_MODE		= (byte)(1<<3);
	private final byte FIXED_ADDRESS	= (byte)(1<<2);
	private final byte READ_KEYS		= (byte)(0b10);
	private final byte DISPLAY_ON		= (byte)(1<<3);

	//	The bits are displayed by mapping shown below:
	//
	//	 -- 0 --
	//	|       |
	//	5       1
	//	 -- 6 --
	//	4       2
	//	|       |
	//	 -- 3 --  .7
	//
	// definition for standard hexadecimal numbers
	private final byte NUMBER_FONT[] = {
	  0b00111111, // 0
	  0b00000110, // 1
	  0b01011011, // 2
	  0b01001111, // 3
	  0b01100110, // 4
	  0b01101101, // 5
	  0b01111101, // 6
	  0b00000111, // 7
	  0b01111111, // 8
	  0b01101111, // 9
	  0b01110111, // A
	  0b01111100, // B
	  0b00111001, // C
	  0b01011110, // D
	  0b01111001, // E
	  0b01110001  // F
	};
	
	public TM1638(JPigpio pigpio, GPIO stbGpio, GPIO clkGpio, GPIO dioGpio) throws PigpioException {
		this.pigpio = pigpio;
		this.stbGpio = stbGpio;
		this.clkGpio = clkGpio;
		this.dioGpio = dioGpio;
		
		isTestMode = false;
		isFixedAddress = false;
		isReadKeys = false;
		isDisplayOn = false;
		brightness = 0b111;
		
		// Validate that the GPIOs are in the correct modes.
		if (stbGpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(stbGpio.getPin());
		}
		if (clkGpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(clkGpio.getPin());
		}
		if (dioGpio.getDirection() != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(dioGpio.getPin());
		}
		
		// Set the clock initially low
		clkGpio.setValue(JPigpio.PI_HIGH);
		setStrobe(true);
		
		
		pigpio.gpioDelay(100);
		setStrobe(false);
		setDataCommand(false, false, false);
		sendDataCommand();
		setStrobe(true);
		
		pigpio.gpioDelay(100);
		setStrobe(false);
		setDisplay(true);
		setBrightness(0b111);
		sendDisplayCommand();
		setStrobe(true);

		pigpio.gpioDelay(100);
		setStrobe(false);
		sendAddress(0);
		for (int i=0; i< 16; i++) {
			writeByte((byte)0b10000000);
		}
		setStrobe(true);
		
	} // End of constructor
	
	/**
	 * Write a digit to the data stream.  A digit is a set of LED segments
	 * lit in such a way that they show a numeric digit.
	 * @param digit The digit to display.  The values are between 0 and 15 inclusive.  Values
	 * between 10 and 15 are the letters a-f corresponding to hex values.
	 * 10 - A
	 * 11 - b
	 * 12 - C
	 * 13 - d
	 * 14 - E
	 * 15 - F
	 * @throws PigpioException
	 */
	private void writeDigit(int digit) throws PigpioException {
		byte value = NUMBER_FONT[digit & 0b1111];
		writeByte(value);
	} // End of writeDigit
	
	public void writeNumber(int number) throws PigpioException {
		assert number >= 0;
		// 12345678
		// 10000000
		int multi = 10000000;
		while(multi >= 10) {
			if (number > multi) {
				break;
			}
			multi = multi / 10;
		}
		setStrobe(false);
		sendAddress(0);
		for (int i=0; i<8; i++) {
			if (multi > 0) {
				writeDigit(number / multi);
				number = number % multi;
				multi = multi / 10;
			} else {
				writeByte((byte)0);
			}
			// Skip the LEDs
			writeByte((byte)(0));
		}
		setStrobe(true);
	}
	
	private void setDataCommand(boolean isTestMode, boolean isFixedAddress, boolean isReadKeys) {
		this.isTestMode = isTestMode;
		this.isFixedAddress = isFixedAddress;
		this.isReadKeys = isReadKeys;
	} // End of setCommand
	
	private void sendDataCommand() throws PigpioException {
		byte value = DATA_COMMAND;
		if (isTestMode) {
			value |= TEST_MODE;
		}
		if (isFixedAddress) {
			value |= FIXED_ADDRESS;
		}
		if (isReadKeys) {
			value |= READ_KEYS;
		}
		writeByte(value);
	} // End of sendDataCommand
	
	private void sendAddress(int address) throws PigpioException {
		byte value = (byte)((address & 0b1111) | DATA_COMMAND);
		writeByte(value);
		
	} // End of setAddress
	
	private void setDisplay(boolean isDisplayOn) {
		this.isDisplayOn = isDisplayOn;
	}
	
	private void setBrightness(int brightness) {
		this.brightness = brightness & 0b111;
	}
	
	private void sendDisplayCommand() throws PigpioException {
		byte value = DISPLAY_COMMAND;
		if (isDisplayOn) {
			value |= DISPLAY_ON;
		}
		value |= (byte)(brightness & 0b111);
		writeByte(value);
	} // End of sendDisplayCommand
	
	private void writeByte(byte value) throws PigpioException {
		pigpio.gpioShiftOut(dioGpio, clkGpio, false, JPigpio.PI_LSBFIRST, value);
	}
	
	private void setStrobe(boolean value) throws PigpioException {
		stbGpio.setValue(value);
	}
} // End of class
// End of file