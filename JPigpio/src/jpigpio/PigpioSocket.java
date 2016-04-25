package jpigpio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	String host;
	int port;

	SocketLock slCmd; // socket for sending commands to PIGPIO

	NotificationListener listener = null;

	public final int PIGPIOD_MESSAGE_SIZE = 12;

	/*
	 * COMMAND cmd p1 p2 p3 Extension
	 */
	private final int CMD_MODES = 0;		//0 gpio mode 0 -
	private final int CMD_MODEG = 1;		//1 gpio 0 0 -
	private final int CMD_PUD = 2;			//2 gpio pud 0 -
	private final int CMD_READ = 3;			//3 gpio 0 0 -
	private final int CMD_WRITE = 4;		//4 gpio level 0 -

	// private final int CMD_PWM = 5;		//5 gpio dutycycle 0 -
	// private final int CMD_PRS = 6;		//6 gpio range 0 -
	// private final int CMD_PFS = 7;		//7 gpio frequency 0 -

	private final int CMD_SERVO = 8;		//8 gpio pulsewidth 0 -
	private final int CMD_WDOG = 9;			//9 gpio timeout 0 -
	private final int CMD_BR1 = 10;			//10 0 0 0 -

	// private final int CMD_BR2 = 11;		//11 0 0 0 -
	// private final int CMD_BC1 = 12;		//12 bits 0 0 -
	// private final int CMD_BC2 = 13;		//13 bits 0 0 -
	// private final int CMD_BS1 = 14;		//14 bits 0 0 -
	// private final int CMD_BS2 = 15;		//15 bits 0 0 -

	private final int CMD_TICK = 16;		//16 0 0 0 -
	// private final int CMD_HWVER = 17;	//17 0 0 0 -
	private final int CMD_NO = 18;			//18 0 0 0 -
	private final int CMD_NB = 19;			//19 handle bits 0 -
	private final int CMD_NP = 20;			//20 handle 0 0 -
	private final int CMD_NC = 21;			//21 handle 0 0 -

	// private final int CMD_PRG = 22;		//22 gpio 0 0 -
	// private final int CMD_PFG = 23;		//23 gpio 0 0 -
	// private final int CMD_PRRG = 24;		//24 gpio 0 0 -
	// private final int CMD_HELP = 25;		//25 N/A N/A N/A N/A
	// private final int CMD_PIGPV = 26;	//26 0 0 0 -

	private final int CMD_WVCLR = 27;		//27 0 0 0 -
	private final int CMD_WVAG = 28;		//28 0 0 12*X gpioPulse_t pulse[X]
	private final int CMD_WVAS = 29;		//29 gpio baud 12+X uint32_t databits uint32_t stophalfbits uint32_t offset uint8_t data[X]

	// private final int CMD_WVGO = 30;		//30 0 0 0 -
	// private final int CMD_WVGOR = 31;	//31 0 0 0 -

	private final int CMD_WVBSY = 32;		//32 0 0 0 -
	private final int CMD_WVHLT = 33;		//33 0 0 0 -

	// private final int CMD_WVSM = 34;		//34 subcmd 0 0 -
	// private final int CMD_WVSP = 35;		//35 subcmd 0 0 -
	// private final int CMD_WVSC = 36;		//36 subcmd 0 0 -
	// private final int CMD_TRIG = 37;		//37 gpio pulselen 4 uint32_t level
	// private final int CMD_PROC = 38;		//38 0 0 X uint8_t text[X]
	// private final int CMD_PROCD = 39;	//39 script_id 0 0 -
	// private final int CMD_PROCR = 40;	//40 script_id 0 4*X uint32_t pars[X]
	// private final int CMD_PROCS = 41;	//41 script_id 0 0 -
	// private final int CMD_SLRO = 42;		//42 gpio baud 4 uint32_t databits
	// private final int CMD_SLR = 43;		//43 gpio count 0 -
	// private final int CMD_SLRC = 44;		//44 gpio 0 0 -
	// private final int CMD_PROCP = 45;	//45 script_id 0 0 -
	// private final int CMD_MICS = 46;		//46 micros 0 0 -

	private final int CMD_MILS = 47;		//47 millis 0 0 -
	// private final int CMD_PARSE = 48;	// 48 N/A N/A N/A N/A
	private final int CMD_WVCRE = 49;		//49 0 0 0
	private final int CMD_WVDEL = 50;		//50 wave_id 0 0
	private final int CMD_WVTX = 51;		//51 wave_id 0 0
	private final int CMD_WVTXR = 52;		//52 wave_id 0 0
	private final int CMD_WVNEW = 53;		//53 0 0 0 -
	private final int CMD_I2CO = 54;		//54 bus device 4 uint32_t flags
	private final int CMD_I2CC = 55;		//55 handle 0 0 -
	private final int CMD_I2CRD = 56;		//56 handle count 0 -
	private final int CMD_I2CWD = 57;		//57 handle 0 X uint8_t data[X]

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

	private final int CMD_NOIB = 99;		//99 0 0 0 -

	/**
	 * Notification listener runs and listens to asynchronous messages received from Pigpio daemon
	 * triggered by subscribing to notifications.
	 * Messages are distributed to subscribed callbacks.
	 */
	class NotificationListener implements Runnable{

		SocketLock slNotify;  // socket for notifications
		SocketLock slPiCmd; // socket for commands

		int handle;
		boolean go = true;
		Thread thread;

		ArrayList<Callback> callbacks = new ArrayList<>();
		int monitor = 0;

		/**
		 * Create notification processing thread and open additional socket on PIGPIO host
		 * for receiving notifications.
		 *
		 * @param host
		 * 	PIGPIO host
		 * @param port
		 * 	PIGPIO port
		 * @throws PigpioException
         */
		public NotificationListener(SocketLock slCmd, String host, int port) throws PigpioException{
			this.slPiCmd = slCmd;
			try {

				// open additional socket used for notifications from Pi
				slNotify = new SocketLock(host, port);

				// open notification handle at PIGPIO
				handle = slNotify.sendCmd(CMD_NOIB, 0, 0);

			} catch (IOException e) {
				throw new PigpioException("NotificationListener", e);
			}

		}

		public void terminate() throws PigpioException {
			if (go) {
				go = false;
				try {
					// send command to stop notifications
					slPiCmd.sendCmd(CMD_NC, handle, 0);
					slNotify.terminate();
				} catch (IOException e) {
					throw new PigpioException("NotificationListener.terminate", e);
				}

			}

		}

		public void append(Callback callback) throws PigpioException{
			try {
				callbacks.add(callback);
				monitor = monitor | callback.bit;
				// send command to start sending notifications for bit-map specified GPIOs
				slPiCmd.sendCmd(CMD_NB, handle, monitor);
			} catch (IOException e) {
				throw new PigpioException("NotificationListener.append", e);
			}
		}

		public void remove(Callback callback) throws PigpioException{
			int newMonitor = 0;

			if (callbacks.remove(callback)){

				// calculate new bit-map in case no other callback monitors PIGPIO of callback being removed
				for (Callback c:callbacks)
					newMonitor |= c.bit;

				// if new bit-map differs, let PIGPIO know
				if (newMonitor != monitor) {
					monitor = newMonitor;
					try {
						slPiCmd.sendCmd(CMD_NB, handle, monitor);
					} catch (IOException e) {
						throw new PigpioException("NotificationListener.remove", e);
					}
				}


			}
		}

		@Override
		public void run(){
			int seq, flags, tick, level;
			int changed;
			int newLevel = 0;
			int gpio = 0;

			try {
				int lastLevel = slPiCmd.sendCmd(CMD_BR1, 0, 0);

				// loop until stop signal is received
				while (this.go) {
					// wait until there is whole message waiting in the input buffer
					while (slNotify.in.available() < PIGPIOD_MESSAGE_SIZE) ;

					seq = Integer.reverseBytes(slNotify.in.readShort());
					flags = Integer.reverseBytes(slNotify.in.readShort());
					tick = Integer.reverseBytes(slNotify.in.readInt());
					level = Integer.reverseBytes(slNotify.in.readInt());

					//System.out.println("Listener - message received: seq="+seq+" flgs="+flags+" tick="+tick+" level="+level);

					// no special flag, so it's normal notification
					if (flags == 0) {
						changed = level ^ lastLevel;
						lastLevel = level;
						for (Callback cb : callbacks) {
							// check if changed GPIO is the one callback is waiting for
							//System.out.println("#1-1 "+String.format("%32s",Integer.toBinaryString(cb.bit)));
							//System.out.println("#1-2 "+String.format("%32s",Integer.toBinaryString(changed)));
							//System.out.println("#1-3 "+String.format("%32s",Integer.toBinaryString(level)));

							if ((cb.bit & changed) != 0) {
								// let's assume new gpio level is "low"
								newLevel = 0;
								//System.out.println("#2 "+changed+" : "+Integer.toBinaryString(cb.bit)+" : "+(cb.bit & changed));
								// if the current state/level is "high"?
								if ((cb.bit & level) != 0)
									newLevel = 1;
								//System.out.println("#3 "+changed+" : "+Integer.toBinaryString(cb.bit)+" : "+(cb.bit & changed));
								if ((cb.edge ^ newLevel) != 0)
									cb.func(cb.gpio, newLevel, tick);

							}
						}
					} else
						// is it a watchdog message?
						if ((flags & PI_NTFY_FLAGS_WDOG) != 0) {
							gpio = flags & PI_NTFY_FLAGS_WDOG;
							for (Callback cb : callbacks)
								if (cb.gpio == gpio)
									cb.func(cb.gpio, PI_TIMEOUT, tick);
					}
				}

				// clean-up
				slNotify.terminate();
			} catch (IOException e) {
				// TODO: handle exception somehow :-)
				//throw new PigpioException("NotificationListener.run",e);

			}

		}

		public void start(){
			if (thread == null)
			{
				thread = new Thread (this);
				thread.start ();
			}
		}

	}



	/**
	 * The constructor of the class.
	 * 
	 * @param host
	 *            The address of the pigpio demon.
	 * @param port
	 *            The port of the pigpio demon.
	 */
	public PigpioSocket(String host, int port) throws PigpioException {
		this.host = host;
		this.port = port;
	}

	/**
	 * Initialize
	 * 
	 * @return
	 */
	@Override
	public void gpioInitialize() throws PigpioException {
		try {
			slCmd = new SocketLock(host, port);
			listener = new NotificationListener(slCmd, host, port);
			listener.start();
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
			// stop listener thread
			//System.out.println("Terminating listener.");
			listener.terminate();
			// stop command socket to pigpio
			//System.out.println("Terminating slCmd.");
			slCmd.terminate();
		} catch (Exception e) {
			throw new PigpioException("gpioTerminate", e);
		}
	} // gpioTerminate

	@Override
	public void gpioSetMode(int pin, int mode) throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_MODES, pin, mode);
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
			int rc = slCmd.sendCmd(CMD_MODEG, pin, 0);
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
			int rc = slCmd.sendCmd(CMD_PUD, pin, pud);
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
			int rc = slCmd.sendCmd(CMD_READ, pin, 0);
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
			int rc = slCmd.sendCmd(CMD_WRITE, pin, value?1:0);
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
			return slCmd.sendCmd(CMD_NO, 0, 0);
		} catch (IOException e) {
			throw new PigpioException("notifyOpen", e);
		}
	}

	@Override
	public int notifyBegin(int handle, int bits) throws PigpioException{
		try {
			return slCmd.sendCmd(CMD_NB, handle, bits);
		} catch (IOException e) {
			throw new PigpioException("notifyBegin", e);
		}
	}

	@Override
	public int notifyPause(int handle) throws PigpioException {
		try {
			return slCmd.sendCmd(CMD_NP, handle, 0);
		} catch (IOException e) {
			throw new PigpioException("notifyPause", e);
		}

	}

	@Override
	public int notifyClose(int handle) throws PigpioException{
		try {
			return slCmd.sendCmd(CMD_NC, handle, 0);
		} catch (IOException e) {
			throw new PigpioException("notifyClose", e);
		}

	}

	@Override
	public int setWatchdog(int userGpio, int timeout) throws PigpioException{
		try {
			return slCmd.sendCmd(CMD_WDOG, userGpio, timeout);
		} catch (IOException e) {
			throw new PigpioException("setWatchdog", e);
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
			return slCmd.sendCmd(CMD_WVCLR, 0, 0);
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

		ByteArrayOutputStream ext = new ByteArrayOutputStream();

		if (pulses == null || pulses.size() == 0)
			return 0;

		try {
			for (Pulse p:pulses)
				ext = Utils.streamReverseInt(ext, p.gpioOn, p.gpioOff, p.delay);

			return slCmd.sendCmd(CMD_WVAG,0,0,pulses.size()*12,ext.toByteArray());

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

		ByteArrayOutputStream ext = new ByteArrayOutputStream();

		if (data.length == 0)
			return 0;

		try {
			// compose ext
			ext = Utils.streamReverseInt(ext, bbBits, bbStop, offset);
			ext.write(data);

			return slCmd.sendCmd(CMD_WVAS, userGpio, baud, data.length + 12, ext.toByteArray());
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
			return slCmd.sendCmd(CMD_WVNEW, 0, 0);
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
	public boolean waveTxBusy() throws PigpioException {
		try {
			return (slCmd.sendCmd(CMD_WVBSY, 0, 0) > 0);
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
			return slCmd.sendCmd(CMD_WVHLT, 0, 0);
		} catch (IOException e) {
			throw new PigpioException("waveTxStop", e);
		}
	} // waveTxBusy

	@Override
	public int waveCreate() throws PigpioException {
		try {
			return slCmd.sendCmd(CMD_WVCRE, 0, 0);
		} catch (IOException e) {
			throw new PigpioException("waveCreate", e);
		}
	}

	@Override
	public int waveDelete(int waveId) throws PigpioException{
		try {
			return slCmd.sendCmd(CMD_WVDEL, 0, 0);
		} catch (IOException e) {
			throw new PigpioException("waveDelete", e);
		}
	}

	@Override
	public int waveSendOnce(int waveId) throws PigpioException {
		try {
			return slCmd.sendCmd(CMD_WVTX, waveId, 0);
		} catch (IOException e) {
			throw new PigpioException("waveSendOnce", e);
		}
	}

	@Override
	public int waveSendRepeat(int waveId) throws PigpioException {
		try {
			return slCmd.sendCmd(CMD_WVTXR, waveId, 0);
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
			return slCmd.sendCmd(CMD_I2CO, i2cBus, i2cAddr);
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
			int rc = slCmd.sendCmd(CMD_I2CC, handle, 0);
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
			int rc = slCmd.sendCmd(CMD_I2CRD, handle, data.length);
			slCmd.readBytes(data);
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
			int rc = slCmd.sendCmd(CMD_I2CWD, handle, 0, data.length, data);
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
			slCmd.sendCmd(CMD_MILS, (int)delay, 0);
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
			return Integer.toUnsignedLong(slCmd.sendCmd(CMD_TICK, 0, 0));
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
			int rc = slCmd.sendCmd(CMD_SERVO, gpio, pulseWidth);
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

	// ############### SPI
	
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

	/**
	 * Calls a user supplied function (a callback) whenever the
	 * specified GPIO edge is detected.
	 *
	 * The user supplied callback receives three parameters, the GPIO,
	 * the level, and the tick.
	 *
	 * If a user callback is not specified a default tally callback is
	 * provided which simply counts edges.  The count may be retrieved
	 * by calling the tally function.  The count may be reset to zero
	 * by calling the reset_tally function.
	 *
	 * The callback may be cancelled by calling the cancel function.
	 *
	 * A GPIO may have multiple callbacks (although I can't think of
	 * a reason to do so).
	 *
	 * ...
	 * def cbf(gpio, level, tick):
	 * print(gpio, level, tick)
	 *
	 * cb1 = pi.callback(22, pigpio.EITHER_EDGE, cbf)
	 *
	 * cb2 = pi.callback(4, pigpio.EITHER_EDGE)
	 *
	 * cb3 = pi.callback(17)
	 *
	 * print(cb3.tally())
	 *
	 * cb3.reset_tally()
	 *
	 * cb1.cancel() # To cancel callback cb1.
	 * ...
     * @param cb
	 * 	user supplied callback object.
     */
	@Override
	public void addCallback(Callback cb) throws PigpioException{
		listener.append(cb);
	}

	@Override
	public void removeCallback(Callback cb) throws PigpioException{
		listener.remove(cb);
	}


} // End of class
// End of file