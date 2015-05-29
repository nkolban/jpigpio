package jpigpio.impl;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

public class SPI {
	private int handle;
	private JPigpio pigpio;
	private boolean debug;

	public SPI(JPigpio pigpio, int channel, int baudRate, int flags) throws PigpioException {
		this.pigpio = pigpio;
		this.debug = false;
		handle = pigpio.spiOpen(channel, baudRate, flags);
	} // End of constructor

	public void close() throws PigpioException {
		pigpio.spiClose(handle);
	} // End of close

	public void read(byte data[]) throws PigpioException {
		pigpio.spiRead(handle, data);
		if (debug) {
			System.out.println("spiRead: " + Utils.dumpData(data));
		}
	} // End of read

	public void write(byte data[]) throws PigpioException {
		if (debug) {
			System.out.println("spiWrite: " + Utils.dumpData(data));
		}
		pigpio.spiWrite(handle, data);

	} // End of write

	public void xfer(byte txData[], byte rxData[]) throws PigpioException {
		if (debug) {
			System.out.print("xfer: " + Utils.dumpData(txData));
		}
		pigpio.spiXfer(handle, txData, rxData);
		if (debug) {
			System.out.println(" " + Utils.dumpData(rxData));
		}
	} // End of xfer
	
	public byte xfer(byte txData) throws PigpioException {
		byte txData1[] = {txData};
		byte rxData[] = new byte[1];
		xfer(txData1, rxData);
		return rxData[0];
	}
} // End of class
// End of file