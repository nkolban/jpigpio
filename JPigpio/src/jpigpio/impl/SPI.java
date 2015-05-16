package jpigpio.impl;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

public class SPI {
	private int handle;
	private JPigpio pigpio;
	
	public SPI(JPigpio pigpio, int channel, int baudRate, int flags) throws PigpioException {
		this.pigpio = pigpio;
		handle = pigpio.spiOpen(channel, baudRate, flags);
	}
	
	public void close() throws PigpioException {
		pigpio.spiClose(handle);
	}
	public void read(byte data[]) throws PigpioException {
		pigpio.spiRead(handle, data);
		System.out.println("spiRead: " + Utils.dumpData(data));
	}
	
	public void write(byte data[]) throws PigpioException {
		System.out.println("spiWrite: " + Utils.dumpData(data));
		pigpio.spiWrite(handle, data);
	}
	
	public void xfer(byte txData[], byte rxData[]) throws PigpioException {
		System.out.print("xfer: " + Utils.dumpData(txData));
		pigpio.spiXfer(handle, txData, rxData);
		System.out.println(" " + Utils.dumpData(rxData));
	}
} // End of class
// End of file