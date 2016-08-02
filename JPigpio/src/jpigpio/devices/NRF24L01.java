package jpigpio.devices;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.WrongModeException;

import static jpigpio.Utils.bytesToHex;

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

	private boolean dynPayloadEnabled = false;
	private int payloadSize = 32;
	private final int MAX_PAYLOAD_SIZE		= 32;

	
	private final int CONFIG_REGISTER		= 0x00;
	private final int EN_AA					= 0x01;
	private final int EN_RXADDR_REGISTER	= 0x02;
	private final int SETUP_AW_REGISTER		= 0x03;
	private final int SETUP_RETR_REGISTER	= 0x04;
	private final int RF_CH_REGISTER		= 0x05;
	private final int RF_SETUP   			= 0x06;
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

	// data rates
	public static final int RF24_250KBPS  = 0b10;
	public static final int RF24_1MBPS    = 0b00;
	public static final int RF24_2MBPS    = 0b01;

	// power levels
	public static final int RF24_PA_MIN   = 0b00;
	public static final int RF24_PA_LOW   = 0b01;
	public static final int RF24_PA_HIGH  = 0b10;
	public static final int RF24_PA_MAX   = 0b11;
	
	public NRF24L01(JPigpio pigpio) {
		this.pigpio = pigpio;
		channel = 76;
		baseConfig = BV(EN_CRC) & ~BV(CRCO);
	}
	
	public boolean init(int cePin, int csnPin) throws PigpioException {
		this.cePin = cePin;
		this.csnPin = csnPin;

		// set specified pins to Output mode
		pigpio.gpioSetMode(cePin, JPigpio.PI_OUTPUT);
		pigpio.gpioSetMode(csnPin, JPigpio.PI_OUTPUT);

		if (pigpio.gpioGetMode(cePin) != JPigpio.PI_OUTPUT)
			throw new WrongModeException(cePin);

		if (pigpio.gpioGetMode(csnPin) != JPigpio.PI_OUTPUT)
			throw new WrongModeException(csnPin);

		ceLow(); // Set the device to RX
		csnHigh(); // Set Slave Select to off
		handle = pigpio.spiOpen(JPigpio.PI_SPI_CHANNEL0, JPigpio.PI_SPI_BAUD_500KHZ, 0);

		byte setupReg = readByteRegister(RF_SETUP);
		// if setup is 0 of 0xff then module does not respond
		if (setupReg == 0 || setupReg == (byte)0xFF)
			return false;

		// reset CONFIG register & set CRC to 32 bit
		configRegister(CONFIG_REGISTER, (byte)0b00001100);

		// Set the address width to be 5 bytes
		setAddressWidths((byte)0b11);

		// enable receiving on pipe 0 and pipe 1
		configRegister(EN_RXADDR_REGISTER, (byte)0b11);

		// Set 1500uS (minimum for 32B payload in ESB@250KBPS) timeouts, to make testing a little easier
		// WARNING: If this is ever lowered, either 250KBS mode with AA is broken or maximum packet
		// sizes must never be used. See documentation for a more complete explanation.
		setRetries(5,15);

		// get status of dynamic payload
		dynPayloadEnabled = ((readByteRegister(FEATURE) & (byte)1<<EN_DPL) == (byte)1<<EN_DPL);

		// Start receiver
		powerUpRx();
		flushRx();

		return true;

	} // End of init

	public void terminate() throws PigpioException {
		pigpio.gpioTerminate();
	}
	
	
	/**
	 * Configure the parameters of the device.
	 * @param payloadSize Number of bytes in payload pipe 0 and pipe 1.
	 * @throws PigpioException
	 */
	public void config(int payloadSize) throws PigpioException {
		// Set length of incoming payload
		configRegister(RX_PW_P0, (byte)payloadSize);
		configRegister(RX_PW_P1, (byte)payloadSize);
		this.payloadSize = payloadSize;

		// Start receiver 
		powerUpRx();
		flushRx();
	} // End of config

	// Sets the receiving address
	public void setRADDR(byte adr[]) throws PigpioException	{
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

	public void setChannel(int ch) throws PigpioException {
		configRegister(RF_CH_REGISTER, (byte)ch);
		channel = (byte)ch;
	}

	public void setDataRate(int dataRate) throws PigpioException{
		byte setupReg = readByteRegister(RF_SETUP);
		byte newValue = setupReg;

		if ((dataRate & 0b10) == 0b10)
			newValue = (byte)(newValue | 1<<RF_DR_LOW);
		else
			newValue = (byte)(newValue & (~(1<<RF_DR_LOW)));

		if ((dataRate & 0b01) == 0b01)
			newValue = (byte)(newValue | (1<<RF_DR_HIGH));
		else
			newValue = (byte)(newValue & (~(1<<RF_DR_HIGH)));

		writeByteRegister(RF_SETUP, newValue);
	}

	public void setPALevel(int level){

	}

	public void setPayloadWidth(int pipe, byte width) throws PigpioException {
		byte regs[] = {RX_PW_P0, RX_PW_P1, RX_PW_P2, RX_PW_P3, RX_PW_P4, RX_PW_P5};

		if (pipe >= 0 && pipe <= 5)
			writeByteRegister(regs[pipe], width);

		if (pipe == 0)
			payloadSize = width;

	}

	public void disableCRC() throws PigpioException {
		setCRCSize(0);
	}

	public void setCRCSize(int crcSize) throws PigpioException {
		switch (crcSize){
			case 0:
				clearRegisterBits(CONFIG_REGISTER, (byte)(1<<EN_CRC));  // disable CRC
				baseConfig = baseConfig & ~(1<<EN_CRC);
				break;
			case 1:
				setRegisterBits(CONFIG_REGISTER, (byte)(1<<EN_CRC));    // enable CRC
				baseConfig = baseConfig | (1<<EN_CRC);
				clearRegisterBits(CONFIG_REGISTER, (byte)(1<<CRCO));    // zero = 16 bit CRC
				baseConfig = baseConfig & ~(1<<CRCO);
				break;
			case 2:
				setRegisterBits(CONFIG_REGISTER, (byte)(1<<EN_CRC));    // enable CRC
				baseConfig = baseConfig | (1<<EN_CRC);
				setRegisterBits(CONFIG_REGISTER, (byte)(1<<CRCO));      // one = 32 bit CRC
				baseConfig = baseConfig | (1<<CRCO);
				break;
		}

	}

	private void setRegisterBits(int reg, byte bits) throws PigpioException {
		byte regVal = readByteRegister(reg);
		byte newVal = regVal;
		newVal = (byte)(newVal | bits);
		writeByteRegister(reg,newVal);
	}

	private void clearRegisterBits(int reg, byte bits) throws PigpioException {
		byte regVal = readByteRegister(reg);
		byte newVal = regVal;
		newVal = (byte)(newVal & ~bits);
		writeByteRegister(reg,newVal);

	}

	public void openReadingPipe(int pipe) throws PigpioException {
		byte rxAddr = readByteRegister(EN_RXADDR_REGISTER);
		if (pipe >= 0 && pipe <= 5)
			writeByteRegister(EN_RXADDR_REGISTER, (byte)(rxAddr | (byte)(1<<pipe)));

	}

	public void closeReadingPipe(int pipe) throws PigpioException {
		byte rxAddr = readByteRegister(EN_RXADDR_REGISTER);
		if (pipe >= 0 && pipe <= 5)
			writeByteRegister(EN_RXADDR_REGISTER, (byte)(rxAddr & (byte)(~(1<<pipe))));

	}

	public void setRetries(int delay, int count) throws PigpioException {
		writeByteRegister(SETUP_RETR_REGISTER,(byte)((delay & 0xf)<<ARD | (count & 0xf)<<ARC));
	}

	// Checks if data is available for reading
	public boolean dataReady() throws PigpioException {
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
	public void getData(byte data[])  throws PigpioException {
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


	// Clocks only one byte into the given MiRF register
	public void configRegister(int reg, byte value) throws PigpioException {
		writeByteRegister(reg, value);
	} // End of configRegister

	public byte readByteRegister(int reg) throws PigpioException{
	   	byte data[] = {NOP};
	   	readRegister(reg,data);
		return data[0];
	}

	// Reads an array of bytes from the given start position in the MiRF registers.
	public void readRegister(int reg, byte value[]) throws PigpioException {
	    nrfSpiWrite((R_REGISTER | (REGISTER_MASK & reg)), value);
	} // End of readRegister

	// Writes an array of bytes into inte the MiRF registers.
	public void writeRegister(int reg, byte data[]) throws PigpioException {
		//System.out.println("Write register: " + reg + ", mask = " + (W_REGISTER | (REGISTER_MASK & reg)) );
		
		// The register value will be 32 + reg number as the coding of writing a register is
		// 0b001x xxxx where "xxxxx" is the 5 bit register number.
		byte clonedData[] = data.clone();
		nrfSpiWrite((W_REGISTER | (REGISTER_MASK & reg)), clonedData);
	} // End of writeRegister

	public void writeByteRegister(int reg, byte value) throws PigpioException {
		byte data[] = {value};
		writeRegister(reg, data);
	}
	
	/**
	 * Send up to 32 bytes of data.
	 * @param value The data to send.
	 * @throws PigpioException
	 */
	public void send(byte value[]) throws PigpioException {
		byte buff[] = value.clone();

		// if fixed payload size and value is shorter than payload size
		if (!dynPayloadEnabled && (buff.length < payloadSize) )
			buff = Arrays.copyOf(buff,payloadSize); // then extend to payload size

		byte status = getStatus();

		// wait for previous transmit to complete
		while (isSending()){

		}

		/*
		while (transmitMode) {
			status = getStatus();

			if((status & ((1 << TX_DS)  | (1 << MAX_RT))) != 0){
				transmitMode = false;
				break;
			}
		} // Wait until last packet is sent
		*/

		ceLow(); // Put the device in RX state

		powerUpTx();       // Set to transmitter mode , Power up
		//flushTx();

		byte fifo = getFIFOStatus();
		p("FIFO Before: "+String.format("%02x",fifo)+"  "+String.format("%8s", Integer.toBinaryString(fifo & 0xFF)).replace(' ', '0'));
		nrfSpiWrite(W_TX_PAYLOAD, buff);   // Write  to TX FIFO register
		fifo = getFIFOStatus();
		p("FIFO After: "+String.format("%02x",fifo)+"  "+String.format("%8s", Integer.toBinaryString(fifo & 0xFF)).replace(' ', '0'));

		p("Sending CE-TX pulse");
		ceHigh();
		pigpio.gpioDelay(100);
		ceLow();
		//cePulse(100); // 15 microseconds long high pulse is more then 10microsec and should trigger transmit mode

		try { Thread.sleep(100); } catch (InterruptedException e) {}
		fifo = getFIFOStatus();
		p("FIFO After TX: "+String.format("%02x",fifo)+"  "+String.format("%8s", Integer.toBinaryString(fifo & 0xFF)).replace(' ', '0'));


	} // End of send
	
	/**
	 * isSending.
	 *
	 * Test if chip is still sending.
	 * When sending has finished return chip to listening.
	 *
	 * @return true if data is currently being transmitted
	 * @throws  PigpioException on pigpiod error
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
		return readByteRegister(STATUS_REGISTER);
	} // End of getStatus
	
	public byte getConfig() throws PigpioException {
		return readByteRegister(CONFIG_REGISTER);
	} // End of getStatus
	
	public byte getRFChannel() throws PigpioException {
		return readByteRegister(RF_CH_REGISTER);
	}
	
	public byte getAddressWidths() throws PigpioException {
		return readByteRegister(SETUP_AW_REGISTER);
	}

	public byte[][] getRXAddresses() throws PigpioException {
		byte addr[][] = new byte[6][5];
		readRegister(RX_ADDR_P0,addr[0]);
		readRegister(RX_ADDR_P1,addr[1]);
		readRegister(RX_ADDR_P2,addr[2]);
		readRegister(RX_ADDR_P3,addr[3]);
		readRegister(RX_ADDR_P4,addr[4]);
		readRegister(RX_ADDR_P5,addr[5]);
		return addr;
	}

	public byte[] getTXAddress() throws PigpioException {
		byte addr[] = new byte[5];
		readRegister(TX_ADDR,addr);
		return addr;
	}

	public void setAddressWidths(int width) throws PigpioException {
		/* Initialize with NOP so we get the first byte read back. */
		configRegister(SETUP_AW_REGISTER, (byte)width);
	}

	public byte getAutomaticRetransmission() throws PigpioException {
		return readByteRegister(SETUP_RETR_REGISTER);
	}
	
	public byte getFIFOStatus() throws PigpioException {
		return readByteRegister(FIFO_STATUS_REGISTER);
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

	private void cePulse(long duration) throws PigpioException {
		pigpio.gpioTrigger(cePin, duration, true);
		p("CE=pulse");
	}
	
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
		switch(value & 0b11) {
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

	public String dataRateToString(int dataRate) {
		switch (dataRate) {
			case RF24_1MBPS:
				return "1 Mbps";
			case RF24_2MBPS:
				return "2 Mbps";
			case RF24_250KBPS:
				return "256 kbps";
			case 0x11:
				return "reserved";
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

	public void printDetails(PrintStream prn){

		try {
			byte statusReg = readByteRegister(STATUS_REGISTER);
			prn.println("STATUS          = 0x" + String.format("%02x",statusReg) + "  " + String.format("%8s", Integer.toBinaryString(statusReg & 0xFF)).replace(' ', '0'));

			byte rxAddr[][] = getRXAddresses();
			prn.println("RX_ADDR_P0-1    = 0x"+ bytesToHex(rxAddr[0])+ "  0x" + bytesToHex(rxAddr[1]));

			prn.print(  "RX_ADDR_P2-5    = ");
			for(int i = 2;i<6;i++)
				prn.print("0x"+String.format("%02x",rxAddr[i][0])+"  ");
			prn.println();

			byte txAddr[] = getTXAddress();
			prn.println("TX_ADDR         = 0x"+ bytesToHex(txAddr));

			byte pwReg[] = {0,0,0,0,0,0};
			readRegister(RX_PW_P0,pwReg);  // read 6 registers/bytes, starting with RX_PW_P0
			prn.print(  "RX_PW_P0-6      = ");
			for(byte b:pwReg)
				prn.print("0x"+String.format("%02x",b)+"  ");
			prn.println();


			byte enAA = readByteRegister(EN_AA);
			prn.println("EN_AA           = 0x"+ String.format("%02x",enAA) + "  " + String.format("%8s", Integer.toBinaryString(enAA & 0xFF)).replace(' ', '0'));

			byte enRX = readByteRegister(EN_RXADDR_REGISTER);
			prn.println("EN_RXADDR       = 0x"+ String.format("%02x",enRX) + "  " + String.format("%8s", Integer.toBinaryString(enRX & 0xFF)).replace(' ', '0'));

			byte rfChannel = getRFChannel();
			prn.println("RF_CH           = 0x"+ String.format("%02x",rfChannel));

			byte setupReg = readByteRegister(RF_SETUP);
			prn.println("RF_SETUP        = 0x"+ String.format("%02x",setupReg) + "  " + String.format("%8s", Integer.toBinaryString(setupReg & 0xFF)).replace(' ', '0'));

			byte configReg = getConfig();
			prn.println("CONFIG          = 0x"+ String.format("%02x",configReg) + "  " + String.format("%8s", Integer.toBinaryString(configReg & 0xFF)).replace(' ', '0'));

			byte dyn = readByteRegister(DYNPD);
			byte feature = readByteRegister(FEATURE);
			prn.println("DYNPD/FEATURE   = 0x"+ String.format("%02x",dyn)+" 0x"+ String.format("%02x",feature));

			byte dataRate = 0;
			if ((setupReg & 1<<RF_DR_LOW) > 0) dataRate += 2;
			if ((setupReg & 1<<RF_DR_HIGH) > 0) dataRate += 1;
			prn.println("Data Rate       = " + dataRateToString(dataRate));

			prn.println("MODEL           = ???");

			prn.print(  "CRC Length      = ");
			if ((configReg & (1<<CRCO)) == (1<<CRCO) )
				prn.println("32 bits");
			else
				prn.println("16 bits");

			prn.println("PA Power        = ???");

			byte retry = readByteRegister(SETUP_RETR_REGISTER);
			prn.println("SETUP_RETR      = 0x"+ String.format("%02x",retry) + "  " + String.format("%8s", Integer.toBinaryString(retry & 0xFF)).replace(' ', '0'));


		} catch (PigpioException e) {
			prn.println(e);
		}
	}
} // End of class
// End of file