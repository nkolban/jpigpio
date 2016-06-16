package jpigpio.sensors;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.WrongModeException;
import jpigpio.impl.SPI;

public class MFRC522 {

	private final byte STATUS_OK = 1; // Success
	private final byte STATUS_ERROR = 2; // Error in communication
	private final byte STATUS_COLLISION = 3; // Collission detected
	private final byte STATUS_TIMEOUT = 4; // Timeout in communication.
	private final byte STATUS_NO_ROOM = 5; // A buffer is not big enough.
	private final byte STATUS_INTERNAL_ERROR = 6; // Internal error in the code. Should not happen ;-)
	private final byte STATUS_INVALID = 7; // Invalid argument.
	private final byte STATUS_CRC_WRONG = 8; // The CRC_A does not match
	private final byte STATUS_MIFARE_NACK = 9; // A MIFARE PICC responded with NAK.

	private final byte CommandReg = 0x01 << 1; // starts and stops command execution
	private final byte ComIEnReg = 0x02 << 1; // enable and disable interrupt request control bits
	private final byte DivIEnReg = 0x03 << 1; // enable and disable interrupt request control bits
	private final byte ComIrqReg = 0x04 << 1; // interrupt request bits
	private final byte DivIrqReg = 0x05 << 1; // interrupt request bits
	private final byte ErrorReg = 0x06 << 1; // error bits showing the error status of the last command executed 
	private final byte Status1Reg = 0x07 << 1; // communication status bits
	private final byte Status2Reg = 0x08 << 1; // receiver and transmitter status bits
	private final byte FIFODataReg = 0x09 << 1; // input and output of 64 byte FIFO buffer
	private final byte FIFOLevelReg = 0x0A << 1; // number of bytes stored in the FIFO buffer
	private final byte WaterLevelReg = 0x0B << 1; // level for FIFO underflow and overflow warning
	private final byte ControlReg = 0x0C << 1; // miscellaneous control registers
	private final byte BitFramingReg = 0x0D << 1; // adjustments for bit-oriented frames
	private final byte CollReg = 0x0E << 1; // bit position of the first bit-collision detected on the RF interface
	//						  0x0F			// reserved for future use

	// Page 1: Command
	// 						  0x10			// reserved for future use
	private final byte ModeReg = 0x11 << 1; // defines general modes for transmitting and receiving 
	private final byte TxModeReg = 0x12 << 1; // defines transmission data rate and framing
	private final byte RxModeReg = 0x13 << 1; // defines reception data rate and framing
	private final byte TxControlReg = 0x14 << 1; // controls the logical behavior of the antenna driver pins TX1 and TX2
	private final byte TxASKReg = 0x15 << 1; // controls the setting of the transmission modulation
	private final byte TxSelReg = 0x16 << 1; // selects the internal sources for the antenna driver
	private final byte RxSelReg = 0x17 << 1; // selects internal receiver settings
	private final byte RxThresholdReg = 0x18 << 1; // selects thresholds for the bit decoder
	private final byte DemodReg = 0x19 << 1; // defines demodulator settings
	// 						  0x1A			// reserved for future use
	// 						  0x1B			// reserved for future use
	private final byte MfTxReg = 0x1C << 1; // controls some MIFARE communication transmit parameters
	private final byte MfRxReg = 0x1D << 1; // controls some MIFARE communication receive parameters
	// 						  0x1E			// reserved for future use
	private final byte SerialSpeedReg = 0x1F << 1; // selects the speed of the serial UART interface

	// Page 2: Configuration
	// 						  0x20			// reserved for future use
	private final byte CRCResultRegH = 0x21 << 1; // shows the MSB and LSB values of the CRC calculation
	private final byte CRCResultRegL = 0x22 << 1;
	// 						  0x23			// reserved for future use
	private final byte ModWidthReg = 0x24 << 1; // controls the ModWidth setting?
	// 						  0x25			// reserved for future use
	private final byte RFCfgReg = 0x26 << 1; // configures the receiver gain
	private final byte GsNReg = 0x27 << 1; // selects the conductance of the antenna driver pins TX1 and TX2 for modulation 
	private final byte CWGsPReg = 0x28 << 1; // defines the conductance of the p-driver output during periods of no modulation
	private final byte ModGsPReg = 0x29 << 1; // defines the conductance of the p-driver output during periods of modulation
	private final byte TModeReg = 0x2A << 1; // defines settings for the internal timer
	private final byte TPrescalerReg = 0x2B << 1; // the lower 8 bits of the TPrescaler value. The 4 high bits are in TModeReg.
	private final byte TReloadRegH = 0x2C << 1; // defines the 16-bit timer reload value
	private final byte TReloadRegL = 0x2D << 1;
	private final byte TCounterValueRegH = 0x2E << 1; // shows the 16-bit timer value
	private final byte TCounterValueRegL = 0x2F << 1;

	// Page 3: Test Registers
	// 						  0x30			// reserved for future use
	private final byte TestSel1Reg = 0x31 << 1; // general test signal configuration
	private final byte TestSel2Reg = 0x32 << 1; // general test signal configuration
	private final byte TestPinEnReg = 0x33 << 1; // enables pin output driver on pins D1 to D7
	private final byte TestPinValueReg = 0x34 << 1; // defines the values for D1 to D7 when it is used as an I/O bus
	private final byte TestBusReg = 0x35 << 1; // shows the status of the internal test bus
	private final byte AutoTestReg = 0x36 << 1; // controls the digital self test
	private final byte VersionReg = 0x37 << 1; // shows the software version
	private final byte AnalogTestReg = 0x38 << 1; // controls the pins AUX1 and AUX2
	private final byte TestDAC1Reg = 0x39 << 1; // defines the test value for TestDAC1
	private final byte TestDAC2Reg = 0x3A << 1; // defines the test value for TestDAC2
	private final byte TestADCReg = 0x3B << 1; // shows the value of ADC I and Q channels
	// 						  0x3C			// reserved for production tests
	// 						  0x3D			// reserved for production tests
	// 						  0x3E			// reserved for production tests
	// 						  0x3F			// reserved for production tests

	private final byte PCD_Idle = 0x00; // no action, cancels current command execution
	private final byte PCD_Mem = 0x01; // stores 25 bytes into the internal buffer
	private final byte PCD_GenerateRandomID = 0x02; // generates a 10-byte random ID number
	private final byte PCD_CalcCRC = 0x03; // activates the CRC coprocessor or performs a self test
	private final byte PCD_Transmit = 0x04; // transmits data from the FIFO buffer
	private final byte PCD_NoCmdChange = 0x07; // no command change, can be used to modify the CommandReg register bits without affecting the command, for example, the PowerDown bit
	private final byte PCD_Receive = 0x08; // activates the receiver circuits
	private final byte PCD_Transceive = 0x0C; // transmits data from FIFO buffer to antenna and automatically activates the receiver after transmission
	private final byte PCD_MFAuthent = 0x0E; // performs the MIFARE standard authentication as a reader
	private final byte PCD_SoftReset = 0x0F; // resets the MFRC522

	private JPigpio pigpio;
	private int chipSelectPin;
	private int resetPowerDownPin;
	private SPI spi;

	/**
	 * Object constructor
	 * @param pigpio JPiogpio object
	 * @param chipSelectPin Chip select pin
	 * @param resetPowerDownPin Reset power down pin
	 * @throws PigpioException
     */
	public MFRC522(JPigpio pigpio, int chipSelectPin, int resetPowerDownPin) throws PigpioException {
		this.pigpio = pigpio;
		this.chipSelectPin = chipSelectPin;
		this.resetPowerDownPin = resetPowerDownPin;
		if (pigpio.gpioGetMode(chipSelectPin) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(chipSelectPin);
		}
		if (pigpio.gpioGetMode(resetPowerDownPin) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(resetPowerDownPin);
		}
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_HIGH);
		pigpio.gpioWrite(resetPowerDownPin, JPigpio.PI_LOW);
		spi = new SPI(pigpio, 0, JPigpio.PI_SPI_BAUD_1MHZ, 0);
	}

	/**
	 * Write single byte to specified register
	 * @param reg The register to write to. One of the PCD_Register enums.
	 * @param value The value to write.
	 * @throws PigpioException
     */
	public void PCD_WriteRegister(byte reg, ///< The register to write to. One of the PCD_Register enums.
			byte value) throws PigpioException ///< The value to write.
	{
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_LOW); // Select slave
		byte data[] = { (byte) (reg & 0x7e), value };
		spi.write(data);
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_HIGH); // Release slave again
	} // End of PCD_WriteRegister

	/**
	 * Write set of values (bytearray) to specified register.
	 * @param reg The register to write to. One of the PCD_Register enums.
	 * @param values The values to write. Byte array.
	 * @throws PigpioException
     */
	public void PCD_WriteRegister(byte reg, ///< The register to write to. One of the PCD_Register enums.
			byte values[] ///< The values to write. Byte array.
	) throws PigpioException {
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_LOW); // Select slave
		byte data[] = new byte[values.length + 1];
		data[0] = (byte) (reg & 0x7E); // MSB == 0 is for writing. LSB is not used in address. Datasheet section 8.1.2.3.
		System.arraycopy(values, 0, data, 1, values.length);
		spi.write(data);
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_HIGH); // Release slave again
	} // End PCD_WriteRegister()

	/**
	 * Reads a byte from the specified register in the MFRC522 chip.
	 * The interface is described in the datasheet section 8.1.2.
	 * @param reg The register to read from. One of the PCD_Register enums.
	 * @return byte from specified register
	 * @throws PigpioException
	 */
	public byte PCD_ReadRegister(byte reg ///< The register to read from. One of the PCD_Register enums.
	) throws PigpioException {
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_LOW); // Select slave
		spi.xfer((byte) (0x80 | (reg & 0x7E))); // MSB == 1 is for reading. LSB is not used in address. Datasheet section 8.1.2.3.
		byte value = spi.xfer((byte) 0); // Read the value back. Send 0 to stop reading.
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_HIGH); // Release slave again
		return value;
	} // End PCD_ReadRegister()

	/**
	 * Reads a number of bytes from the specified register in the MFRC522 chip.
	 * The interface is described in the datasheet section 8.1.2.
	 * @param reg The register to read from. One of the PCD_Register enums.
	 * @param rxAlign Byte array to store the values in.
	 * @param values Only bit positions rxAlign..7 in values[0] are updated.
	 * @throws PigpioException
	 */
	public void PCD_ReadRegister(byte reg, ///< The register to read from. One of the PCD_Register enums.
			byte values[], ///< Byte array to store the values in.
			byte rxAlign ///< Only bit positions rxAlign..7 in values[0] are updated.
	) throws PigpioException {
		if (values.length == 0) {
			return;
		}
		//Serial.print(F("Reading ")); 	Serial.print(count); Serial.println(F(" bytes from register."));
		byte address = (byte) (0x80 | (reg & 0x7E)); // MSB == 1 is for reading. LSB is not used in address. Datasheet section 8.1.2.3.
		byte index = 0; // Index in values array.
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_LOW); // Select slave								// One read is performed outside of the loop
		spi.xfer(address); // Tell MFRC522 which address we want to read
		while (index < (values.length - 1)) {
			if (index == 0 && rxAlign != 0) { // Only update bit positions rxAlign..7 in values[0]
				// Create bit mask for bit positions rxAlign..7
				byte mask = 0;
				for (byte i = rxAlign; i <= 7; i++) {
					mask |= (1 << i);
				}
				// Read value and tell that we want to read the same address again.
				byte value = spi.xfer(address);
				// Apply mask to both current value of values[0] and the new data in value.
				values[0] = (byte) ((values[index] & ~mask) | (value & mask));
			} else { // Normal case
				values[index] = spi.xfer(address); // Read value and tell that we want to read the same address again.
			}
			index++;
		}
		values[index] = spi.xfer((byte) 0); // Read the final byte. Send 0 to stop reading.
		pigpio.gpioWrite(chipSelectPin, JPigpio.PI_HIGH); // Release slave again
	} // End PCD_ReadRegister()

	/**
	 * Sets the bits given in mask in register reg.
	 * @param reg The register to update. One of the PCD_Register enums.
	 * @param mask The bits to set.
	 * @throws PigpioException
	 */
	public void PCD_SetRegisterBitMask(byte reg, ///< The register to update. One of the PCD_Register enums.
			byte mask ///< The bits to set.
	) throws PigpioException {
		byte tmp;
		tmp = PCD_ReadRegister(reg);
		PCD_WriteRegister(reg, (byte) (tmp | mask)); // set bit mask
	} // End PCD_SetRegisterBitMask()

	/**
	 * Clears the bits given in mask from register reg.
	 * @param mask The bits to clear.
	 * @param reg The register to update. One of the PCD_Register enums.
	 * @throws PigpioException
	 */
	public void PCD_ClearRegisterBitMask(byte reg, ///< The register to update. One of the PCD_Register enums.
			byte mask ///< The bits to clear.
	) throws PigpioException {
		byte tmp;
		tmp = PCD_ReadRegister(reg);
		PCD_WriteRegister(reg, (byte) (tmp & (~mask))); // clear bit mask
	} // End PCD_ClearRegisterBitMask()

	/**
	 * Use the CRC coprocessor in the MFRC522 to calculate a CRC_A.
	 *
	 * @param data Pointer to the data to transfer to the FIFO for CRC calculation.
	 * @param length The number of bytes to transfer.
	 * @param result Pointer to result buffer. Result is written to result[0..1], low byte first.
	 * @return STATUS_OK on success, STATUS_??? otherwise.
	 * @throws PigpioException
	 */
	public byte PCD_CalculateCRC(byte data[], ///< In: Pointer to the data to transfer to the FIFO for CRC calculation.
			byte length, ///< In: The number of bytes to transfer.
			byte result[] ///< Out: Pointer to result buffer. Result is written to result[0..1], low byte first.
	) throws PigpioException {
		PCD_WriteRegister(CommandReg, PCD_Idle); // Stop any active command.
		PCD_WriteRegister(DivIrqReg, (byte) 0x04); // Clear the CRCIRq interrupt request bit
		PCD_SetRegisterBitMask(FIFOLevelReg, (byte) 0x80); // FlushBuffer = 1, FIFO initialization
		PCD_WriteRegister(FIFODataReg, data); // Write data to the FIFO
		PCD_WriteRegister(CommandReg, PCD_CalcCRC); // Start the calculation

		// Wait for the CRC calculation to complete. Each iteration of the while-loop takes 17.73�s.
		int i = 5000;
		byte n;
		while (true) {
			n = PCD_ReadRegister(DivIrqReg); // DivIrqReg[7..0] bits are: Set2 reserved reserved MfinActIRq reserved CRCIRq reserved reserved
			if ((n & 0x04) != 0) { // CRCIRq bit set - calculation done
				break;
			}
			if (--i == 0) { // The emergency break. We will eventually terminate on this one after 89ms. Communication with the MFRC522 might be down.
				return STATUS_TIMEOUT;
			}
		}
		PCD_WriteRegister(CommandReg, PCD_Idle); // Stop calculating CRC for new content in the FIFO.

		// Transfer the result from the registers to the result buffer
		result[0] = PCD_ReadRegister(CRCResultRegL);
		result[1] = PCD_ReadRegister(CRCResultRegH);
		return STATUS_OK;
	} // End PCD_CalculateCRC()
	
	/**
	 * Initializes the MFRC522 chip.
	 * @throws PigpioException
	 */
	public void PCD_Init() throws PigpioException {
		if (pigpio.gpioRead(resetPowerDownPin) == JPigpio.PI_LOW) {	//The MFRC522 chip is in power down mode.
			pigpio.gpioWrite(resetPowerDownPin, JPigpio.PI_HIGH);		// Exit power down mode. This triggers a hard reset.
			// Section 8.8.2 in the datasheet says the oscillator start-up time is the start up time of the crystal + 37,74�s. Let us be generous: 50ms.
			pigpio.gpioDelay(50, JPigpio.PI_MILLISECONDS);
		}
		else { // Perform a soft reset
			PCD_Reset();
		}
		
		// When communicating with a PICC we need a timeout if something goes wrong.
		// f_timer = 13.56 MHz / (2*TPreScaler+1) where TPreScaler = [TPrescaler_Hi:TPrescaler_Lo].
		// TPrescaler_Hi are the four low bits in TModeReg. TPrescaler_Lo is TPrescalerReg.
		PCD_WriteRegister(TModeReg, (byte)0x80);			// TAuto=1; timer starts automatically at the end of the transmission in all communication modes at all speeds
		PCD_WriteRegister(TPrescalerReg, (byte)0xA9);		// TPreScaler = TModeReg[3..0]:TPrescalerReg, ie 0x0A9 = 169 => f_timer=40kHz, ie a timer period of 25�s.
		PCD_WriteRegister(TReloadRegH, (byte)0x03);		// Reload timer with 0x3E8 = 1000, ie 25ms before timeout.
		PCD_WriteRegister(TReloadRegL, (byte)0xE8);
		
		PCD_WriteRegister(TxASKReg, (byte)0x40);		// Default 0x00. Force a 100 % ASK modulation independent of the ModGsPReg register setting
		PCD_WriteRegister(ModeReg, (byte)0x3D);		// Default 0x3F. Set the preset value for the CRC coprocessor for the CalcCRC command to 0x6363 (ISO 14443-3 part 6.2.4)
		PCD_AntennaOn();						// Enable the antenna driver pins TX1 and TX2 (they were disabled by the reset)
	} // End PCD_Init()
	
	/**
	 * Performs a soft reset on the MFRC522 chip and waits for it to be ready again.
	 * @throws PigpioException
	 */
	public void PCD_Reset() throws PigpioException {
		PCD_WriteRegister(CommandReg, PCD_SoftReset);	// Issue the SoftReset command.
		// The datasheet does not mention how long the SoftRest command takes to complete.
		// But the MFRC522 might have been in soft power-down mode (triggered by bit 4 of CommandReg) 
		// Section 8.8.2 in the datasheet says the oscillator start-up time is the start up time of the crystal + 37,74�s. Let us be generous: 50ms.
		pigpio.gpioDelay(50, JPigpio.PI_MILLISECONDS);
		// Wait for the PowerDown bit in CommandReg to be cleared
		while ((PCD_ReadRegister(CommandReg) & (1<<4)) != 0) {
			// PCD still restarting - unlikely after waiting 50ms, but better safe than sorry.
		}
	} // End PCD_Reset()
	
	/**
	 * Turns the antenna on by enabling pins TX1 and TX2.
	 * After a reset these pins are disabled.
	 * @throws PigpioException
	 */
	public void PCD_AntennaOn() throws PigpioException {
		byte value = PCD_ReadRegister(TxControlReg);
		if ((value & 0x03) != 0x03) {
			PCD_WriteRegister(TxControlReg, (byte)(value | 0x03));
		}
	} // End PCD_AntennaOn()
	
	/**
	 * Turns the antenna off by disabling pins TX1 and TX2.
	 * @throws PigpioException
	 */
	public void PCD_AntennaOff() throws PigpioException {
		PCD_ClearRegisterBitMask(TxControlReg, (byte)0x03);
	} // End PCD_AntennaOff()
	
	/**
	 * Get the current MFRC522 Receiver Gain (RxGain[2:0]) value.
	 * See 9.3.3.6 / table 98 in http://www.nxp.com/documents/data_sheet/MFRC522.pdf
	 * NOTE: Return value scrubbed with (0x07&lt;&lt;4)=01110000b as RCFfgReg may use reserved bits.
	 * 
	 * @return Value of the RxGain, scrubbed to the 3 bits used.
	 * @throws PigpioException
	 */
	public byte PCD_GetAntennaGain() throws PigpioException {
		return (byte)(PCD_ReadRegister(RFCfgReg) & (0x07<<4));
	} // End PCD_GetAntennaGain()
	
	/**
	 * Set the MFRC522 Receiver Gain (RxGain) to value specified by given mask.
	 * See 9.3.3.6 / table 98 in http://www.nxp.com/documents/data_sheet/MFRC522.pdf
	 * NOTE: Given mask is scrubbed with (0x07&lt;&lt;4)=01110000b as RCFfgReg may use reserved bits.
	 * @param mask
	 * @throws PigpioException
	 */
	public void PCD_SetAntennaGain(byte mask) throws PigpioException {
		if (PCD_GetAntennaGain() != mask) {						// only bother if there is a change
			PCD_ClearRegisterBitMask(RFCfgReg, (byte)(0x07<<4));		// clear needed to allow 000 pattern
			PCD_SetRegisterBitMask(RFCfgReg, (byte)(mask & (0x07<<4)));	// only set RxGain[2:0] bits
		}
	} // End PCD_SetAntennaGain()
} // End of class
// End of file