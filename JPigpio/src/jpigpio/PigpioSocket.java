package jpigpio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import jpigpio.impl.CommonPigpio;

/**
 * http://abyz.co.uk/rpi/pigpio/sif.html
 */

/**
 * An implementation of the Pigpio Java interface using sockets to connect to the target pigpio demon.
 *
 */
public class PigpioSocket extends CommonPigpio {
	/**
	 * A data output stream over which to write data.
	 */
	private DataOutputStream dataOutputStream;

	/**
	 * A data input stream over which to read data.
	 */
	private DataInputStream dataInputStream;

	/*
	 * COMMAND cmd p1 p2 p3 Extension
	 */
	// CMD_MODES
	// 0 gpio mode 0 -
	private final int CMD_MODES = 0;

	// CMD_MODEG
	// 1 gpio 0 0 -
	private final int CMD_MODEG = 1;

	// CMD_PUD
	// 2 gpio pud 0 -
	private final int CMD_PUD = 2;

	// CMD_READ
	// 3 gpio 0 0 -
	private final int CMD_READ = 3;

	// CMD_WRITE
	// 4 gpio level 0 -
	private final int CMD_WRITE = 4;

	// CMD_PWM
	// 5 gpio dutycycle 0 -
	// private final int CMD_PWM = 5;

	// CMD_PRS
	// 6 gpio range 0 -
	// private final int CMD_PRS = 6;

	// CMD_PFS
	// 7 gpio frequency 0 -
	// private final int CMD_PFS = 7;

	// CMD_SERVO
	// 8 gpio pulsewidth 0 -
	private final int CMD_SERVO = 8;

	// CMD_WDOG
	// 9 gpio timeout 0 -
	// private final int CMD_WDOG = 9;

	// CMD_BR1
	// 10 0 0 0 -
	// private final int CMD_BR1 = 10;

	// CMD_BR2
	// 11 0 0 0 -
	// private final int CMD_BR2 = 11;

	// CMD_BC1
	// 12 bits 0 0 -
	// private final int CMD_BC1 = 12;

	// CMD_BC2
	// 13 bits 0 0 -
	// private final int CMD_BC2 = 13;

	// CMD_BS1
	// 14 bits 0 0 -
	// private final int CMD_BS1 = 14;

	// CMD_BS2
	// 15 bits 0 0 -
	// private final int CMD_BS2 = 15;

	// CMD_TICK
	// 16 0 0 0 -
	private final int CMD_TICK = 16;

	// CMD_HWVER
	// 17 0 0 0 -
	// private final int CMD_HWVER = 16;

	// CMD_NO
	// 18 0 0 0 -
	private final int CMD_NO = 18;

	// CMD_NB
	// 19 handle bits 0 -
	private final int CMD_NB = 19;

	// CMD_NP
	// 20 handle 0 0 -
	private final int CMD_NP = 20;

	// CMD_NC
	// 21 handle 0 0 -
	private final int CMD_NC = 21;

	// CMD_PRG
	// 22 gpio 0 0 -
	// private final int CMD_PRG = 16;

	// CMD_PFG
	// 23 gpio 0 0 -
	// private final int CMD_PFG = 16;

	// CMD_PRRG
	// 24 gpio 0 0 -
	// private final int CMD_PRRG = 16;

	// CMD_HELP
	// 25 N/A N/A N/A N/A
	// private final int CMD_HELP = 16;

	// CMD_PIGPV
	// 26 0 0 0 -
	// private final int CMD_PIGPV = 16;

	// CMD_WVCLR - waveClear
	// 27 0 0 0 -
	private final int CMD_WVCLR = 27;

	// CMD_WVAG - waveAddGeneric
	// 28 0 0 12*X gpioPulse_t pulse[X]
	private final int CMD_WVAG = 28;

	// CMD_WVAS - waveAddSerial
	// 29 gpio baud 12+X uint32_t databits uint32_t stophalfbits uint32_t offset uint8_t data[X]
	private final int CMD_WVAS = 29;

	// CMD_WVGO
	// 30 0 0 0 -
	// private final int CMD_WVGO = 16;

	// CMD_WVGOR
	// 31 0 0 0 -
	// private final int CMD_WVGOR = 16;

	// CMD_WVBSY
	// 32 0 0 0 -
	private final int CMD_WVBSY = 32;

	// CMD_WVHLT
	// 33 0 0 0 -
	private final int CMD_WVHLT = 33;

	// CMD_WVSM
	// 34 subcmd 0 0 -
	// private final int CMD_WVSM = 16;

	// CMD_WVSP
	// 35 subcmd 0 0 -
	// private final int CMD_WVSP = 16;

	// CMD_WVSC
	// 36 subcmd 0 0 -
	// private final int CMD_WVSC = 16;

	// CMD_TRIG
	// 37 gpio pulselen 4 uint32_t level
	// private final int CMD_TRIG = 16;

	// CMD_PROC
	// 38 0 0 X uint8_t text[X]
	// private final int CMD_PROC = 16;

	// CMD_PROCD
	// 39 script_id 0 0 -
	// private final int CMD_PROCD = 16;

	// CMD_PROCR
	// 40 script_id 0 4*X uint32_t pars[X]
	// private final int CMD_PROCR = 16;

	// CMD_PROCS
	// 41 script_id 0 0 -
	// private final int CMD_PROCS = 16;

	// CMD_SLRO
	// 42 gpio baud 4 uint32_t databits SLR 43 gpio count 0 -
	// private final int CMD_SLRO = 16;

	// CMD_SLRC
	// 44 gpio 0 0 -
	// private final int CMD_SLRC = 16;

	// CMD_PROCP
	// 45 script_id 0 0 -
	// private final int CMD_PROCP = 16;

	// CMD_MICS
	// 46 micros 0 0 -
	// private final int CMD_MICS = 16;

	// gpioDelay - CMD_MILS
	// 47 millis 0 0 -
	private final int CMD_MILS = 47;

	// PARSE 48 N/A N/A N/A N/A

	// CMD_WVCRE waveCreate (py:
	// 49 0 0 0
	private final int CMD_WVCRE = 49;

	// CMD_WVDEL
	// 50 wave_id 0 0
	private final int CMD_WVDEL = 50;

	// CMD_WVTX
	// 51 wave_id 0 0
	private final int CMD_WVTX = 51;

	// CMD_WVTXR
	// 52 wave_id 0 0
	private final int CMD_WVTXR = 52;

	// CMD_WVNEW - waveAddNew (py: wave_add_new)
	// 53 0 0 0 -
	private final int CMD_WVNEW = 53;

	//
	// 2cOpen - CMD_I2CO
	// 54 bus device 4 uint32_t flags
	//
	private final int CMD_I2CO = 54;

	//
	// i2cClose - CMD_I2CC
	// 55 handle 0 0 -
	//
	private final int CMD_I2CC = 55;

	//
	// i2cReadDevice - CMD_I2CRD
	// 56 handle count 0 -
	//
	private final int CMD_I2CRD = 56;

	//
	// i2cWriteDevice - CMD_I2CWD
	// 57 handle 0 X uint8_t data[X]
	//
	private final int CMD_I2CWD = 57;

	// CMD_I2CWQ 58 handle bit 0 -
	// CMD_I2CRS 59 handle 0 0 -
	// CMD_I2CWS 60 handle byte 0 -
	// CMD_I2CRB 61 handle register 0 -
	// CMD_I2CWB 62 handle register 4 uint32_t byte
	// CMD_I2CRW 63 handle register 0 -
	// CMD_I2CWW 64 handle register 4 uint32_t word
	// CMD_I2CRK 65 handle register 0 -
	// CMD_I2CWK 66 handle register X uint8_t bvs[X]
	// CMD_I2CRI 67 handle register 4 uint32_t num
	// CMD_I2CWI 68 handle register X uint8_t bvs[X]
	// CMD_I2CPC 69 handle register 4 uint32_t word
	// CMD_I2CPK 70 handle register X uint8_t data[X]
	// CMD_SPIO 71 channel baud 4 uint32_t flags
	// CMD_SPIC 72 handle 0 0 -
	// CMD_SPIR 73 handle count 0 -
	// CMD_SPIW 74 handle 0 X uint8_t data[X]
	// CMD_SPIX 75 handle 0 X uint8_t data[X]
	// CMD_SERO 76 baud flags X uint8_t device[X]
	// CMD_SERC 77 handle 0 0 -
	// CMD_SERRB 78 handle 0 0 -
	// CMD_SERWB 79 handle byte 0 -
	// CMD_SERR 80 handle count 0 -
	// CMD_SERW 81 handle 0 X uint8_t data[X]
	// CMD_SERDA 82 handle 0 0 -
	// CMD_GDC 83 gpio 0 0 -
	// CMD_GPW 84 gpio 0 0 -
	// CMD_HC 85 gpio frequency 0 -
	// CMD_HP 86 gpio frequency 4 uint32_t dutycycle
	// CMD_CF1 87 arg1 arg2 X uint8_t argx[X]
	// CMD_CF2 88 arg1 retMax X uint8_t argx[X]

	// CMD_NOIB
	// 99 0 0 0 -
	private final int CMD_NOIB = 99;

	/**
	 * The hostname or IP address of the pigpio demon
	 */
	private String address;

	/**
	 * The port number on which the pigpio demon is listening.
	 */
	private int port;
	private Socket toPigpio;

	/**
	 * Notification listener runs and listens to asynchronous messages received from Pigpio daemon
	 * triggered by subscribing to notifications.
	 * Messages are distributed to subscribed callbacks.
	 */
	class NotificationListener implements Runnable{

		DataInputStream streamNotifyIn;
		DataOutputStream streamNotifyOut;
		Socket piSocket;
		int handle;
		boolean go = true;

		ArrayList<PiCallback> callbacks;
		int monitor = 0;

		public NotificationListener(String host, int port) throws PigpioException{
			try {
				// open additional socket used for notifications from Pi
				piSocket = new Socket(host, port);

				streamNotifyOut = new DataOutputStream(piSocket.getOutputStream());
				streamNotifyIn = new DataInputStream(piSocket.getInputStream());

				// open notification handle at PIGPIO
				handle = sendCmd(CMD_NOIB, 0, 0);

			} catch (IOException e) {
				throw new PigpioException("NotificationListener", e);
			}

		}

		public void stop() throws PigpioException {
			if (go) {
				go = false;
				try {
					streamNotifyOut.writeInt(Integer.reverseBytes(CMD_NC));
					streamNotifyOut.writeInt(Integer.reverseBytes(handle));
					streamNotifyOut.writeInt(Integer.reverseBytes(0));
					streamNotifyOut.writeInt(Integer.reverseBytes(0));
					streamNotifyOut.flush();
				} catch (IOException e) {
					throw new PigpioException("NotificationListener.stop", e);
				}

			}

		}

		public void append(PiCallback callback) throws PigpioException{
			try {
				callbacks.add(callback);
				monitor = monitor | callback.dataBit;
				sendCmd(CMD_NB, handle, monitor);
			} catch (IOException e) {
				throw new PigpioException("NotificationListener.append", e);
			}
		}

		public void run(){
			// TODO: implement

		}

	}



	/**
	 * The constructor of the class.
	 * 
	 * @param address
	 *            The address of the pigpio demon.
	 * @param port
	 *            The port of the pigpio demon.
	 */
	public PigpioSocket(String address, int port) throws PigpioException {
		this.address = address;
		this.port = port;
		gpioInitialize();
	}

	private synchronized int sendCmd(int cmd, int p1, int p2) throws IOException {
		int resp;
		dataOutputStream.writeInt(Integer.reverseBytes(cmd));
		dataOutputStream.writeInt(Integer.reverseBytes(p1));
		dataOutputStream.writeInt(Integer.reverseBytes(p2));
		dataOutputStream.writeInt(Integer.reverseBytes(0));
		dataOutputStream.flush();

		resp = dataInputStream.readInt(); // ignore response
		resp = dataInputStream.readInt(); // ignore response
		resp = dataInputStream.readInt(); // ignore response
		resp = dataInputStream.readInt(); // contains error or response
		return Integer.reverseBytes(resp);
	}

	private synchronized int writeCmdExt(int cmd, int p1, int p2, int p3, int... ext) throws IOException {
		int resp;
		dataOutputStream.writeInt(Integer.reverseBytes(cmd));
		dataOutputStream.writeInt(Integer.reverseBytes(p1));
		dataOutputStream.writeInt(Integer.reverseBytes(p2));
		dataOutputStream.writeInt(Integer.reverseBytes(p3));
		for (int i:ext)
			dataOutputStream.writeInt(Integer.reverseBytes(i));
		dataOutputStream.flush();

		resp = dataInputStream.readInt(); // ignore response
		resp = dataInputStream.readInt(); // ignore response
		resp = dataInputStream.readInt(); // ignore response
		resp = dataInputStream.readInt(); // contains error or response
		return Integer.reverseBytes(resp);
	}



	/**
	 * Write an integer to the pigpio demon
	 * 
	 * @param i
	 *            The value of the integer to write.
	 * @throws IOException
	 */
	private void writeInt(int i) throws IOException {
		dataOutputStream.writeInt(Integer.reverseBytes(i));
		dataOutputStream.flush();
		// byte data[] = new byte[4];
		// data[0] = (byte) (i & 0xff);
		// data[1] = (byte) ((i >> 8) & 0xff);
		// data[2] = (byte) ((i >> 16) & 0xff);
		// data[3] = (byte) ((i >> 24) & 0xff);
		// dataOutputStream.write(data);
	} // End of writeInt

	/**
	 * Write a sequence of ints to the pigpio demon.
	 * 
	 * @param args
	 *            A set of ints to write to the pigpio demon.
	 * @throws IOException
	 */
	private void writeInt(int... args) throws IOException {
		for (int i : args) {
			writeInt(i);
		}
	} // End of writeInt

	/**
	 * Write a sequence of bytes to the pigpio demon.
	 * 
	 * @param data
	 *            The sequence of bytes to write.
	 * @throws IOException
	 */
	private void writeBytes(byte data[]) throws IOException {
		dataOutputStream.write(data);
	} // End of writeBytes

	/**
	 * Read an integer from the pigpio demon.
	 * 
	 * @return
	 * @throws IOException
	 */
	public int readInt() throws IOException {
		// Read 4 bytes of data
		int r = dataInputStream.readInt();
		// System.out.println("Read: " + Integer.reverseBytes(r));

		// Change the endian to Java
		return Integer.reverseBytes(r);
	} // End of readInt

	public void readBytes(byte[] data) throws IOException {
		dataInputStream.readFully(data);
	} // End of readBytes

	@SuppressWarnings("unused")
	public int readPigpioResponse() throws IOException {
		int cmd = readInt();
		int p1 = readInt();
		int p2 = readInt();
		int resp = readInt();
		return resp;
	} // End of readPigpioResponse

	/**
	 * Initialize
	 * 
	 * @return
	 */
	@Override
	public void gpioInitialize() throws PigpioException {
		try {
			toPigpio = new Socket(address, port);
			dataOutputStream = new DataOutputStream(toPigpio.getOutputStream());
			dataInputStream = new DataInputStream(toPigpio.getInputStream());
		} catch (Exception e) {
			throw new PigpioException("gpioInitialize", e);
		}
	} // End of gpioInitialize()

	/**
	 * Terminate the usage of the pigpio interfaces.
	 */
	@Override
	public void gpioTerminate() throws PigpioException {
		try {
			if (toPigpio != null) {
				dataOutputStream.close();
				dataInputStream.close();
				toPigpio.close();
				dataOutputStream = null;
				dataInputStream = null;
				toPigpio = null;
			}
		} catch (Exception e) {
			throw new PigpioException("gpioTerminate", e);
		}
	} // gpioTerminate

	@Override
	public void gpioSetMode(int pin, int mode) throws PigpioException {
		try {
			writeInt(CMD_MODES, pin, mode, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("gpioSetMode", e);
		}
	} // End of gpioSetMode

	@Override
	public int gpioGetMode(int pin) throws PigpioException {
		try {
			writeInt(CMD_MODEG, pin, 0, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
			return rc;
		} catch (IOException e) {
			throw new PigpioException("gpioGetMode", e);
		}
	} // End of gpioGetMode

	@Override
	public void gpioSetPullUpDown(int pin, int pud) throws PigpioException {
		try {
			writeInt(CMD_PUD, pin, pud, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("gpioSetPullUpDown", e);
		}
	} // End of gpioSetPullUpDown

	@Override
	public boolean gpioRead(int pin) throws PigpioException {
		try {
			writeInt(CMD_READ, pin, 0, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
			return rc != 0;
		} catch (IOException e) {
			throw new PigpioException("gpioRead", e);
		}
	}

	@Override
	public void gpioWrite(int pin, boolean value) throws PigpioException {
		try {
			writeInt(CMD_WRITE, pin, value?1:0, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("gpioWrite", e);
		}
	}

	// ############### NOTIFICATIONS

	@Override
	public int notifyOpen() throws PigpioException {
		try {
			writeInt(CMD_NO, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("notifyOpen", e);
		}
	}

	@Override
	public int notifyBegin(int handle, int bits) throws PigpioException{
		try {
			writeInt(CMD_NB, handle, bits, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("notifyBegin", e);
		}
	}

	@Override
	public int notifyPause(int handle) throws PigpioException {
		try {
			writeInt(CMD_NP, handle, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("notifyPause", e);
		}

	}

	@Override
	public int notifyClose(int handle) throws PigpioException{
		try {
			writeInt(CMD_NC, handle, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("notifyClose", e);
		}

	}

	// ################ WAVES

	/**
	 * This function clears all waveforms and any data added by calls to the wave_add_* functions.
	 *
	 * @return The return code from close.
	 */
	@Override
	public int waveClear() throws PigpioException {
		try {
			writeInt(CMD_WVCLR, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveClear", e);
		}
	} // waveClear

	@Override
	public int waveAddGeneric(ArrayList<Pulse> pulses) throws PigpioException{
		// pigpio message format

		// I p1 0
		// I p2 0
		// I p3 pulses * 12
		// ## extension ##
		// III on/off/delay * pulses

		byte[] ext;

		if (pulses == null || pulses.size() == 0)
			return 0;

		try {
			writeInt(CMD_WVAG,0,0,pulses.size()*12);
			for (Pulse p:pulses)
				writeInt(p.gpioOn, p.gpioOff, p.delay);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveAddGeneric", e);
		}

	}

	@Override
	public int waveAddSerial(int userGpio, int baud, byte[] data, int offset, int bbBits, int bbStop) throws PigpioException {

		// pigpio message format

		// I p1 gpio
		// I p2 baud
		// I p3 len+12
		// ## extension ##
		// I bb_bits
		// I bb_stop
		// I offset
		// s len data bytes

		if (data.length == 0)
			return 0;

		try {
			writeInt(CMD_WVAS, userGpio, baud, data.length + 12);
			writeInt(bbBits, bbStop, offset);
			writeBytes(data);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveAddSerial", e);
		}

	}

	/**
	 * Starts a new empty waveform.
	 *
	 * You would not normally need to call this function as it is
	 * automatically called after a waveform is created with the
	 * [*wave_create*] function.
	 *
	 * ...
	 * pi.wave_add_new()
	 * ...
	 *
	 * @return The return code from add new.
	 */
	@Override
	public int waveAddNew() throws PigpioException {
		try {
			writeInt(CMD_WVNEW, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveAddNew", e);
		}
	} // waveAddNew

	/**
	 * Returns 1 if a waveform is currently being transmitted,
	 * otherwise 0.
	 *
	 * ...
	 * pi.wave_send_once(0) # send first waveform
	 *
	 * while pi.wave_tx_busy(): # wait for waveform to be sent
	 * time.sleep(0.1)
	 *
	 * pi.wave_send_once(1) # send next waveform
	 * ...
	 * @return The return code from wave_tx_busy.
	 */
	@Override
	public int waveTxBusy() throws PigpioException {
		try {
			writeInt(CMD_WVBSY, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveTxBusy", e);
		}
	} // waveTxBusy

	/**
	 * Stops the transmission of the current waveform.
	 *
	 * This function is intended to stop a waveform started with
	 * wave_send_repeat.
	 *
	 * ...
	 * pi.wave_send_repeat(3)
	 *
	 * time.sleep(5)
	 *
	 * pi.wave_tx_stop()
	 * ...
	 * @return The return code from wave_tx_stop.
	 */
	@Override
	public int waveTxStop() throws PigpioException {
		try {
			writeInt(CMD_WVHLT, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveTxStop", e);
		}
	} // waveTxBusy

	@Override
	public int waveCreate() throws PigpioException {
		try {
			writeInt(CMD_WVCRE, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveCreate", e);
		}
	}

	@Override
	public int waveDelete(int waveId) throws PigpioException{
		try {
			writeInt(CMD_WVDEL, 0, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveDelete", e);
		}
	}

	@Override
	public int waveSendOnce(int waveId) throws PigpioException {
		try {
			writeInt(CMD_WVTX, waveId, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveSendOnce", e);
		}
	}

	@Override
	public int waveSendRepeat(int waveId) throws PigpioException {
		try {
			writeInt(CMD_WVTXR, waveId, 0, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("waveSendRepeat", e);
		}
	}

	//############### I2C

	/**
	 * Open a connection to the i2c
	 * 
	 * @param i2cBus
	 *            The id of the bus (1 for pi)
	 * @param i2cAddr
	 *            The address of the device on the bus
	 * @return The handle for the device on the bus.
	 */
	@Override
	public int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException {
		try {
			writeInt(CMD_I2CO, i2cBus, i2cAddr, 0);
			return readPigpioResponse();
		} catch (IOException e) {
			throw new PigpioException("i2cOpen", e);
		}
	} // i2cOpen

	/**
	 * Close a previously opened i2c handle.
	 * 
	 * @param handle
	 *            The handle of the previously opened i2c
	 * @return The return code from the close.
	 */
	@Override
	public void i2cClose(int handle) throws PigpioException {
		try {
			writeInt(CMD_I2CC, handle, 0, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("i2cClose", e);
		}
	} // i2cClose

	/**
	 * Read data from the device.
	 * 
	 * @param handle
	 *            The handle to the device from which to read.
	 * @param data
	 *            The data array into which to store the data.
	 */
	@Override
	public int i2cReadDevice(int handle, byte[] data) throws PigpioException {
		try {
			writeInt(CMD_I2CRD, handle, data.length, 0);
			int rc = readPigpioResponse();
			readBytes(data);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
			return rc;
		} catch (IOException e) {
			throw new PigpioException("i2cReadDevice", e);
		}
	} // End of i2cReadDevice

	/**
	 * Write data to the i2c device.
	 * 
	 * @param handle
	 *            The handle of the device to write.
	 * @param data
	 *            The data to write to the device.
	 */
	@Override
	public void i2cWriteDevice(int handle, byte[] data) throws PigpioException {
		try {
			writeInt(CMD_I2CWD, handle, 0, data.length);
			writeBytes(data);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("i2cWriteDevice");
		}
	} // End of i2cWriteDevice

	/**
	 * Delay
	 * 
	 * @param delay
	 *            The time for the delay.
	 */
	@Override
	public void gpioDelay(long delay) throws PigpioException {
		try {
			writeInt(CMD_MILS, (int)delay, 0, 0);
			readPigpioResponse();
			return;
		} catch (IOException e) {
			throw new PigpioException("gpioDelay", e);
		}
	} // End of gpioDelay

	/**
	 * Return the number of microseconds since the PI booted.
	 * 
	 * @return The number of microseconds since the PI booted.
	 */
	@Override
	public long gpioTick() throws PigpioException {
		try {
			writeInt(CMD_TICK, 0, 0, 0);
			return Integer.toUnsignedLong(readPigpioResponse());
		} catch (IOException e) {
			throw new PigpioException("gpioTick", e);
		}
	} // End of gpioTick

	@Override
	public long getCurrentTick() throws PigpioException {
		return gpioTick();
	} // End of getCurrentTick

	/**
	 * Set the pulse width of a specific GPIO.  The pulse width is in microseconds
	 * with a value between 500 and 2500 or a value of 0 to switch the servo off.
	 * @param gpio The pin to use to control the servo.
	 * @param pulseWidth The pulse width of the pulse (500-2500).
	 */
	@Override
	public void gpioServo(int gpio, int pulseWidth) throws PigpioException {
		try {
			writeInt(CMD_SERVO, gpio, pulseWidth, 0);
			int rc = readPigpioResponse();
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("gpioServo", e);
		}
	} // End of gpioServo

	/**
	 * 
	 * @param pin
	 * @param alert
	 * @throws PigpioException
	 */
	@Override
	public void gpioSetAlertFunc(int pin, Alert alert) throws PigpioException {
		throw new NotImplementedException();
	} // End of gpioSetAlertFunc

	@Override
	public void gpioTrigger(int gpio, long pulseLen, boolean level) throws PigpioException {
		throw new NotImplementedException();
	}
	
	/**
	 * Open an SPI channel.
	 * @param spiChannel The channel to open.
	 * @param spiBaudRate The baud rate for transmition and receiption
	 * @param flags Control flags
	 * @return A handle used in subsequent SPI API calls
	 * @throws PigpioException
	 */
	@Override
	public int spiOpen(int spiChannel, int spiBaudRate, int flags) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}


	/**
	 * Close an SPI connection previously created with spiOpen().
	 * @param handle The handle to be closed.
	 * @throws PigpioException
	 */
	@Override
	public void spiClose(int handle) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	

	/**
	 * Read data from SPI.
	 * @param handle The handle from which to read.
	 * @param data An array into which to read data.
	 * @return The number of bytes actually read.
	 * @throws PigpioException
	 */
	@Override
	public int spiRead(int handle, byte[] data) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	/**
	 * Write data to SPI.
	 * @param handle The handle into which to write.
	 * @param data An array of data to write to SPI.
	 * @return The number of bytes actually written
	 * @throws PigpioException
	 */
	@Override
	public int spiWrite(int handle, byte[] data) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	/**
	 * Write data to SPI and in parallel, read responses.  The size of the txData and rxData arrays must
	 * be the same.
	 * @param handle The handle into which to write.
	 * @param txData An array of data to write.
	 * @param rxData An array of data to read.
	 * @return The number of bytes actually transferred.
	 * @throws PigpioException
	 */
	@Override
	public int spiXfer(int handle, byte[] txData, byte[] rxData) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void setDebug(boolean flag) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	@Override
	public long gpioxPulseAndWait(int outGpio, int inGpio, long waitDuration, long pulseHoldDuration, boolean pulseLow) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

} // End of class
// End of file