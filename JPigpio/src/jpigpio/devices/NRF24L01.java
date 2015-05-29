package jpigpio.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.WrongModeException;

/**
 * The NRF24L is a wireless communication device.
 * @author kolban
 *
 */
//@SuppressWarnings("unused")
public class NRF24L01 {
	private JPigpio pigpio;
	/**
	 * CE which is Chip Enable has nothing to do with CSI.  This pin is driven high for a TX operation
	 * and low for a RX operation.
	 */
	private int cePin;
	/**
	 * CSN is the SPI Slave Select ... we believe CSN = Chip Select NOT.
	 */
	private int csnPin;
	private byte channel;
	private int handle;
	private boolean transmitMode;
	private int baseConfig;
	
	private final int CONFIG_REGISTER		= 0x00;
	private final int EN_AA					= 0x01;
	private final int EN_RXADDR_REGISTER	= 0x02;
	private final int SETUP_AW_REGISTER		= 0x03;
	private final int SETUP_RETR_REGISTER	= 0x04;
	private final int RF_CH_REGISTER		= 0x05;
	private final int RF_SETUP   = 0x06;
	private final int STATUS_REGISTER		= 0x07;
	private final int OBSERVE_TX = 0x08;
	private final int CD         = 0x09;
	private final int RX_ADDR_P0 = 0x0A;
	private final int RX_ADDR_P1 = 0x0B;
	private final int RX_ADDR_P2 = 0x0C;
	private final int RX_ADDR_P3 = 0x0D;
	private final int RX_ADDR_P4 = 0x0E;
	private final int RX_ADDR_P5 = 0x0F;
	private final int TX_ADDR    = 0x10;
	private final int RX_PW_P0   = 0x11;
	private final int RX_PW_P1   = 0x12;
	private final int RX_PW_P2   = 0x13;
	private final int RX_PW_P3   = 0x14;
	private final int RX_PW_P4   = 0x15;
	private final int RX_PW_P5   = 0x16;
	private final int FIFO_STATUS_REGISTER= 0x17;
	private final int DYNPD       =0x1C;
	private final int FEATURE    = 0x1D;

	/* Bit Mnemonics */
	private final int MASK_RX_DR  =6;
	private final int MASK_TX_DS = 5;
	private final int MASK_MAX_RT= 4;
	private final int EN_CRC     = 3;
	private final int CRCO       = 2;
	private final int PWR_UP     = 1;
	private final int PRIM_RX    = 0;
	private final int ENAA_P5    = 5;
	private final int ENAA_P4    = 4;
	private final int ENAA_P3    = 3;
	private final int ENAA_P2    = 2;
	private final int ENAA_P1    = 1;
	private final int ENAA_P0    = 0;
	private final int ERX_P5     = 5;
	private final int ERX_P4     = 4;
	private final int ERX_P3      =3;
	private final int ERX_P2     = 2;
	private final int ERX_P1     = 1;
	private final int ERX_P0     = 0;
	private final int AW         = 0;
	private final int ARD        = 4;
	private final int ARC        = 0;
	private final int RF_DR_LOW  = 5;
	private final int PLL_LOCK   = 4;
	private final int RF_DR_HIGH = 3;
	private final int RF_PWR     = 1;
	private final int LNA_HCURR =  0 ;       
	private final int RX_DR      = 6; // RX Data ready status bit
	private final int TX_DS      = 5; // TX Data ready status bit
	private final int MAX_RT     = 4; // Maximum number of TX retransmits reached status bit
	private final int RX_P_NO    = 1;
	private final int TX_FULL    = 5; // TX FIFO full status bit
	private final int PLOS_CNT  =  4;
	private final int ARC_CNT   =  0;
	private final int TX_REUSE  =  6;
	private final int FIFO_FULL =  5;
	private final int TX_EMPTY   = 4;
	private final int RX_FULL     =1;
	private final int RX_EMPTY   = 0;
	private final int DPL_P5     = 5;
	private final int DPL_P4    =  4;
	private final int DPL_P3    =  3;
	private final int DPL_P2    =  2;
	private final int DPL_P1    =  1;
	private final int DPL_P0    =  0;
	private final int EN_DPL    =  2;
	private final int EN_ACK_PAY=  1;
	private final int EN_DYN_ACK = 0;

	/* Instruction Mnemonics */
	private final int R_REGISTER	= 0x00; // Command to read a register
	private final int W_REGISTER	= 0x20;
	private final int REGISTER_MASK	= 0x1F; // Register is LSB 5 bits 0000 0000 -> 0001 1111
	private final int ACTIVATE		= 0x50;
	private final int R_RX_PL_WID	= 0x60;
	private final int R_RX_PAYLOAD	= 0x61;
	private final int W_TX_PAYLOAD	= 0xA0;
	private final int FLUSH_TX		= 0xE1;
	private final int FLUSH_RX		= 0xE2;
	private final int REUSE_TX_PL	= 0xE3;
	private final byte NOP			= (byte)0xFF;
	
	public NRF24L01(JPigpio pigpio) {
		this.pigpio = pigpio;
		channel = 1;
		baseConfig = BV(EN_CRC) & ~BV(CRCO);
	}
	
	public void init(int cePin, int csnPin) throws PigpioException {
		this.cePin = cePin;
		this.csnPin = csnPin;
		if (pigpio.gpioGetMode(cePin) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(cePin);
		}
		if (pigpio.gpioGetMode(csnPin) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(csnPin);
		}

		ceLow(); // Set the device to RX
		csnHigh(); // Set Slave Select to off
		handle = pigpio.spiOpen(JPigpio.PI_SPI_CHANNEL0, JPigpio.PI_SPI_BAUD_500KHZ, 0);
	} // End of init
	
	
	/**
	 * Configure the parameters of the device.
	 * @param payloadSize Number of bytes in payload pipe 0 and pipe 1.
	 * @throws PigpioException
	 */
	public void config(int payloadSize) throws PigpioException {
		// Set RF channel
		configRegister(RF_CH_REGISTER, channel);

		// Set length of incoming payload 
		configRegister(RX_PW_P0, (byte)payloadSize);
		configRegister(RX_PW_P1, (byte)payloadSize);
		
		// Set the address width to be 5 bytes
		configRegister(SETUP_AW_REGISTER, (byte)0b11);
		
		configRegister(EN_RXADDR_REGISTER, (byte)0b11);

		// Start receiver 
		powerUpRx();
		flushRx();
	} // End of config
	
	public void setRADDR(byte adr[]) throws PigpioException
	// Sets the receiving address
	{
		ceLow();
		writeRegister(RX_ADDR_P1,adr);
		ceHigh();
	}
	
	public void setTADDR(byte adr[]) throws PigpioException
	// Sets the transmitting address
	{
		/*
		 * RX_ADDR_P0 must be set to the sending addr for auto ack to work.
		 */
		writeRegister(RX_ADDR_P0,adr);
		writeRegister(TX_ADDR,adr);
	} // End of setTADDR
	
	public boolean dataReady() throws PigpioException
	// Checks if data is available for reading
	{
		// See note in getData() function - just checking RX_DR isn't good enough
		byte status = getStatus();

		// We can short circuit on RX_DR, but if it's not set, we still need
		// to check the FIFO for any pending packets
		if ((status & BV(RX_DR)) != 0) {
			return true;
		}

		return !rxFifoEmpty();
	} // End of dataReady
	
	private boolean rxFifoEmpty() throws PigpioException {
		byte fifoStatus[] = { NOP };

		readRegister(FIFO_STATUS_REGISTER, fifoStatus);

		return (fifoStatus[0] & BV(RX_EMPTY)) != 0;
	}
	
	/**
	 * Read data received into the array
	 * @param data The array of data to be returned.
	 * @throws PigpioException
	 */
	public void getData(byte data[])  throws PigpioException
	// Reads payload bytes into data array
	{
		nrfSpiWrite(R_RX_PAYLOAD, data); // Read payload

		// NVI: per product spec, p 67, note c:
		//  "The RX_DR IRQ is asserted by a new packet arrival event. The procedure
		//  for handling this interrupt should be: 1) read payload through SPI,
		//  2) clear RX_DR IRQ, 3) read FIFO_STATUS to check if there are more 
		//  payloads available in RX FIFO, 4) if there are more data in RX FIFO,
		//  repeat from step 1)."
		// So if we're going to clear RX_DR here, we need to check the RX FIFO
		// in the dataReady() function
		configRegister(STATUS_REGISTER, (byte)BV(RX_DR));   // Reset status register
	} // End of getData
	
	
	private void configRegister(int reg, byte value) throws PigpioException
	// Clocks only one byte into the given MiRF register
	{
		byte data[] = { value };
		writeRegister(reg, data);
	} // End of configRegister
	
	private void readRegister(int reg, byte value[]) throws PigpioException
	// Reads an array of bytes from the given start position in the MiRF registers.
	{
	    nrfSpiWrite((R_REGISTER | (REGISTER_MASK & reg)), value);
	} // End of readRegister
	
	private void writeRegister(int reg, byte data[]) throws PigpioException
	// Writes an array of bytes into inte the MiRF registers.
	{
		//System.out.println("Write register: " + reg + ", mask = " + (W_REGISTER | (REGISTER_MASK & reg)) );
		
		// The register value will be 32 + reg number as the coding of writing a register is
		// 0b001x xxxx where "xxxxx" is the 5 bit register number.
		byte clonedData[] = data.clone();
		nrfSpiWrite((W_REGISTER | (REGISTER_MASK & reg)), clonedData);
	} // End of writeRegister
	
	/**
	 * Send up to 32 bytes of data.
	 * @param value The data to send.
	 * @throws PigpioException
	 */
	public void send(byte value[]) throws PigpioException
	// Sends a data package to the default address. Be sure to send the correct
	// amount of bytes as configured as payload on the receiver.
	{
		assert value != null;
		assert value.length <= 32;
		
		byte status = getStatus();

		while (transmitMode) {
			status = getStatus();

			if((status & ((1 << TX_DS)  | (1 << MAX_RT))) != 0){
				transmitMode = false;
				break;
			}
		} // Wait until last packet is sent

		ceLow(); // Put the device in RX state

		powerUpTx();       // Set to transmitter mode , Power up
		flushTx();

		nrfSpiWrite(W_TX_PAYLOAD, value);   // Write payload

		ceHigh(); // Start transmission
		pigpio.gpioDelay(20);
		ceLow();
	} // End of send
	
	/**
	 * isSending.
	 *
	 * Test if chip is still sending.
	 * When sending has finished return chip to listening.
	 *
	 */

	public boolean isSending() throws PigpioException {
		if(transmitMode){
			byte status = getStatus();
		    	
			/*
			 *  if sending successful (TX_DS) or max retries exceeded (MAX_RT).
			 */

			if((status & ((1 << TX_DS)  | (1 << MAX_RT))) != 0){
				powerUpRx();
				return false; 
			}

			return true;
		}
		return false;
	} // End of isSensing
	
	public byte getStatus() throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		byte data[] = { NOP };
		readRegister(STATUS_REGISTER, data);
		return data[0];
	} // End of getStatus
	
	public byte getConfig() throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		byte data[] = { NOP };
		readRegister(CONFIG_REGISTER, data);
		return data[0];
	} // End of getStatus
	
	public byte getRFChannel() throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		byte data[] = { NOP };
		readRegister(RF_CH_REGISTER, data);
		return data[0];
	} // End of getStatus
	
	public byte getAddressWidths() throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		byte data[] = { NOP };
		readRegister(SETUP_AW_REGISTER, data);
		return data[0];
	} // End of getStatus
	
	public byte getAutomaticRetransmission() throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		byte data[] = { NOP };
		readRegister(SETUP_RETR_REGISTER, data);
		return data[0];
	}
	
	public byte getFIFOStatus() throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		byte data[] = { NOP };
		readRegister(FIFO_STATUS_REGISTER, data);
		return data[0];
	}
	
	private void flushTx() throws PigpioException {
		nrfSpiWrite(FLUSH_TX, null);
	} // End of flushTx
	
	private void powerUpRx() throws PigpioException {
		transmitMode = false;
		ceLow();
		p("Powering up RX");
		p("Setting CONFIG to: " + configToString((byte)(baseConfig | BV(PWR_UP) | BV(PRIM_RX))));
		configRegister(CONFIG_REGISTER, (byte)(baseConfig | BV(PWR_UP) | BV(PRIM_RX) | BV(MASK_RX_DR)));
		//pigpio.gpioDelay(200, JPigpio.PI_MILLISECONDS);
		configRegister(STATUS_REGISTER, (byte)(BV(RX_DR) | BV(TX_DS) | BV(MAX_RT))); 

		ceHigh();
	} // End of powerUpRx
	
	private void flushRx() throws PigpioException {
		nrfSpiWrite(FLUSH_RX, null);
	} // End of flushRx
	
	private void powerUpTx() throws PigpioException {
		transmitMode = true;
		p("Setting CONFIG to: " + configToString((byte)(baseConfig | BV(PWR_UP) & ~BV(PRIM_RX) | BV(MASK_RX_DR))));
		configRegister(CONFIG_REGISTER, (byte)(baseConfig | BV(PWR_UP) & ~BV(PRIM_RX) | BV(MASK_RX_DR)));
		pigpio.gpioDelay(200, JPigpio.PI_MILLISECONDS);
	} // End of powerUpTx

	private void nrfSpiWrite(int reg, byte data[]) throws PigpioException {
		csnLow();
		//System.out.println("nrfSpiWrite: reg: " + reg + ", data: " + toList(data));

		//pigpio.spiXfer(handle, data, data);
		byte regData[] = { (byte)reg };
		pigpio.spiXfer(handle, regData, regData);
		if (data != null) {
			pigpio.spiXfer(handle, data, data);
		}
		csnHigh();
		pigpio.gpioDelay(100, JPigpio.PI_MILLISECONDS);
	} // End of nrfSpiWrite
	
	/**
	 * Put the device into a TX state
	 * @throws PigpioException
	 */
	private void ceHigh() throws PigpioException {
		pigpio.gpioWrite(cePin, JPigpio.PI_HIGH);
		p("CE=1");
	} // End of ceHigh
	
	/**
	 * Put the device in a RX state
	 * @throws PigpioException
	 */
	private void ceLow() throws PigpioException {
		pigpio.gpioWrite(cePin, JPigpio.PI_LOW);
		p("CE=0");
	} // End of ceLow
	
	/**
	 * Disable the device (Slave Select)
	 * @throws PigpioException
	 */
	private void csnHigh() throws PigpioException {
		pigpio.gpioWrite(csnPin, JPigpio.PI_HIGH);
	} // End of csnHigh
	
	/**
	 * Enable the device (Slave Select)
	 * @throws PigpioException
	 */
	private void csnLow() throws PigpioException {
		pigpio.gpioWrite(csnPin, JPigpio.PI_LOW);
	} // End of csnLow
	
	/**
	 * Set a bit
	 * @param bit
	 * @return An integer with the specific bit set.
	 */
	private int BV(int bit) {
		return 1 << bit;
	} // End of BV
	
	public String statusToString(byte status) {
		String ret = "";
		ret += "RX ready: " + bitSet(status, RX_DR);
		ret += ", TX ready: " + bitSet(status, TX_DS);
		ret += ", Max retries: " + bitSet(status, MAX_RT);
		ret += ", Pipe: " + ((status & 0b1110) >> 1);
		ret += ", TX FIFO full: " + bitSet(status, TX_FULL);
		return ret;
	}
	
	public String configToString(byte value) {
		String ret = "";
		ret += "RX IRQ allowed: " + bitSet(value, MASK_RX_DR);
		ret += ", TX IRQ allowed: " + bitSet(value, MASK_TX_DS);
		ret += ", Max retransmits IRQ allowed: " + bitSet(value, MASK_MAX_RT);
		ret += ", CRC enabled: " + bitSet(value, EN_CRC);
		ret += ", CRC encoding scheme: " + bitSet(value, CRCO);
		ret += ", Power up: " + bitSet(value, PWR_UP);
		ret += ", RX(1)/TX(0): " + bitSet(value, PRIM_RX);
		return ret;
	}
	
	public String setupRetrToString(byte value) {
		String ret = "";
		ret += "Delay: " + (((value & 0b11110000) >> 4) * 250 + 250);
		ret += ", Auto retransmit count: " + (value & 0b1111);
		return ret;
	}
	
	public String fifoStatusToString(byte value) {
		String ret = "";
		ret += "Reuse: " + bitSet(value, TX_REUSE);
		ret += ", TX Full: " + bitSet(value, TX_FULL);
		ret += ", TX Empty: " + bitSet(value, TX_EMPTY);
		ret += ", RX Full: " + bitSet(value, RX_FULL);
		ret += ", RX Empty: " + bitSet(value, RX_EMPTY);
		return ret;
	}
	
	
	public String setupAddressWidthToString(byte value) {
		switch(value & 0x11) {
		case 0b00:
			return "Illegal value";
		case 0b01:
			return "3 bytes";
		case 0b10:
			return "4 bytes";
		case 0b11:
			return "5 bytes";
		}
		return "???";
	}
	
	public String bitSet(byte value, int bit) {
		return ((value & BV(bit))!=0)?"1":"0";
	}
	
	public List<Byte> toList(byte data[]) {
		List<Byte> byteList = new ArrayList<>();
		for (byte b: data) {
			byteList.add(b);
		}
		return byteList;
	}
	
	private void p(String text) {
		System.out.println(text);
	}
} // End of class
// End of file