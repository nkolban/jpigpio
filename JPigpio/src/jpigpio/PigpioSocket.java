package jpigpio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import jpigpio.impl.CommonPigpio;

import static jpigpio.Utils.LEint2Long;

/**
 * An implementation of the Pigpio Java interface using sockets to connect to the target pigpio demon
 * socket interface (see http://abyz.co.uk/rpi/pigpio/sif.html)
 * <br><br>
 * See {@link JPigpio JPigpio interface} for full documentation
 *
 */
public class PigpioSocket extends CommonPigpio {

	String host;
	int port;

	SocketLock slCmd; // socket for sending commands to PIGPIO

	NotificationRouter router = null;

	public final int PIGPIOD_MESSAGE_SIZE = 12;

	/*
	 * COMMAND cmd p1 p2 p3 Extension
	 */
	private final int CMD_MODES = 0;		//0 gpio mode 0 -
	private final int CMD_MODEG = 1;		//1 gpio 0 0 -
	private final int CMD_PUD = 2;			//2 gpio pud 0 -
	private final int CMD_READ = 3;			//3 gpio 0 0 -
	private final int CMD_WRITE = 4;		//4 gpio level 0 -

	private final int CMD_PWM = 5;			//5 gpio dutycycle 0 -
	private final int CMD_PRS = 6;			//6 gpio range 0 -
	private final int CMD_PFS = 7;			//7 gpio frequency 0 -

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

	private final int CMD_PRG = 22;			//22 gpio 0 0 -
	private final int CMD_PFG = 23;			//23 gpio 0 0 -
	private final int CMD_PRRG = 24;		//24 gpio 0 0 -

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
	private final int CMD_TRIG = 37;		//37 gpio pulselen 4 uint32_t level
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

	private final int CMD_SPIO = 71; 		// 71 channel baud 4 uint32_t flags
	private final int CMD_SPIC = 72;		// 72 handle 0 0 -
	private final int CMD_SPIR = 73;		// 73 handle count 0 -
	private final int CMD_SPIW = 74;		// 74 handle 0 X uint8_t data[X]
	private final int CMD_SPIX = 75;		// 75 handle 0 X uint8_t data[X]

	private final int CMD_SERO = 76;		// 76 baud flags X uint8_t device[X]
	private final int CMD_SERC = 77;		// 77 handle 0 0 -
	private final int CMD_SERRB = 78;		// 78 handle 0 0 -
	private final int CMD_SERWB = 79;		// 79 handle byte 0 -
	private final int CMD_SERR = 80;		// 80 handle count 0 -
	private final int CMD_SERW = 81;		// 81 handle 0 X uint8_t data[X]
	private final int CMD_SERDA = 82;		// 82 handle 0 0 -

	private final int CMD_GDC = 83;			// 83 gpio 0 0 -

	// CMD_GPW 84 gpio 0 0 -
	// CMD_HC 85 gpio frequency 0 -
	// CMD_HP 86 gpio frequency 4 uint32_t dutycycle
	// CMD_CF1 87 arg1 arg2 X uint8_t argx[X]
	// CMD_CF2 88 arg1 retMax X uint8_t argx[X]
	// CMD_BI2CC	89	sda	0	0	-
	// CMD_BI2CO	90	sda	scl	4	uint32_t baud
	// CMD_BI2CZ	91	sda	0	X	uint8_t data[X]
	// CMD_I2CZ	92	handle	0	X	uint8_t data[X]
	// CMD_WVCHA	93	0	0	X	uint8_t data[X]
	// CMD_SLRI	94	gpio	invert	0	-
	// CMD_CGI	95	0	0	0	-
	// CMD_CSI	96	config	0	0	-
	// CMD_FG	97	gpio	steady	0	-
	// CMD_FN	98	gpio	steady	4	uint32_t active
	// CMD_NOIB	99	0	0	0	-
	// CMD_WVTXM	100	wave_id	mode	0	-
	// CMD_WVTAT	101	-	-	0	-



	private final int CMD_NOIB = 99;		//99 0 0 0 -

	/**
	 * Notification router runs in the background thread and listens to asynchronous messages
	 * received from Pigpio daemon triggered by subscribing to notifications.
	 * Messages are routed to relevant subscribed notificationListeners.
	 */
	class NotificationRouter implements Runnable{

		SocketLock slNotify;  // socket for notifications
		SocketLock slPiCmd; // socket for commands

		int handle;
		boolean go = true;
		Thread thread;

		ArrayList<GPIOListener> gpioListeners = new ArrayList<>();
		int monitor = 0;

		String host;
		int port;

		/**
		 * Create notification processing thread and open additional socket on PIGPIO host
		 * for receiving notifications.
		 *
		 * @param host
		 * 	PIGPIOD host
		 * @param port
		 * 	PIGPIOD port
		 * @throws PigpioException
         */
		public NotificationRouter(SocketLock slCmd, String host, int port) throws PigpioException{
			this.slPiCmd = slCmd;
			this.host = host;
			this.port = port;
			reconnect();
		}

		public void reconnect() throws PigpioException{
			try {

				// open additional socket used for notifications from Pi
				if (slNotify != null)
					slNotify.reconnect();
				else
					slNotify = new SocketLock(host,port);

				// open notification handle at PIGPIO
				handle = slNotify.sendCmd(CMD_NOIB, 0, 0);

			} catch (IOException e) {
				throw new PigpioException("NotificationRouter", e);
			}

		}

        /**
         * Tells Pigpiod to stop sending notifications & closes notification socket connection
         * @throws PigpioException
         */
        public void terminate() throws PigpioException {
			if (go) {
				go = false;
				try {
					// send command to stop notifications
					slPiCmd.sendCmd(CMD_NC, handle, 0);
					slNotify.terminate();
				} catch (IOException e) {
					throw new PigpioException("NotificationRouter.terminate", e);
				}

			}

		}

        /**
         * Add GPIOListener object to the list of objects to call when notification is received
         * @param gpioListener GPIOListener object to be added to the list
         * @throws PigpioException
         */
        public void addListener(GPIOListener gpioListener) throws PigpioException{
			try {
				gpioListeners.add(gpioListener);
				monitor = monitor | gpioListener.bit;
				// send command to start sending notifications for bit-map specified GPIOs
				slPiCmd.sendCmd(CMD_NB, handle, monitor);
			} catch (IOException e) {
				throw new PigpioException("NotificationRouter.addListener", e);
			}
		}

        /**
         * Remove object from the list of GPIOListener objects to be notified
         * @param gpioListener GPIOListener object to be removed from the list.
         * @throws PigpioException
         */
        public void removeListener(GPIOListener gpioListener) throws PigpioException{
			int newMonitor = 0;

			if (gpioListeners.remove(gpioListener)){

				// calculate new bit-map in case no other notificationListener monitors PIGPIO of notificationListener being removed
				for (GPIOListener c: gpioListeners)
					newMonitor |= c.bit;

				// if new bit-map differs, let PIGPIO know
				if (newMonitor != monitor) {
					monitor = newMonitor;
					try {
						slPiCmd.sendCmd(CMD_NB, handle, monitor);
					} catch (IOException e) {
						throw new PigpioException("NotificationRouter.removeListener", e);
					}
				}


			}
		}

		@Override
		public void run(){
			int seq, flags,level;
			long tick;
			int changed;
			int newLevel = 0;
			int gpio = 0;
			byte[] bytes = new byte[4];

			try {
				// read GPIO status for GPIOs in bank 1 (gpio 0-31)
				int lastLevel = slPiCmd.sendCmd(CMD_BR1, 0, 0);

				// loop until stop signal is received
				while (this.go) {
					// wait until there is whole message waiting in the input buffer
					while (go && (slNotify.in.available() < PIGPIOD_MESSAGE_SIZE)) {
                        Thread.sleep(20);
                    }

					if (!go) // if stopping, then exit loop (to avoid big if)
						break;

					seq = Integer.reverseBytes(slNotify.in.readUnsignedShort());
					flags = Integer.reverseBytes(slNotify.in.readUnsignedShort());

					slNotify.in.read(bytes,0,4);  // read tick as plain 4 bytes first
					// tick is stored as 4 byte unsigned integer using Little Endian byte order
					// so we need to transform it to long
					tick = LEint2Long(bytes);

					level = Integer.reverseBytes(slNotify.in.readInt());

					// no special flag, so it's normal notification
					if (flags == 0) {
						changed = level ^ lastLevel;
						lastLevel = level;
						for (GPIOListener cb : gpioListeners) {
							// check if changed GPIO is the one listener is waiting for
							if ((cb.bit & changed) != 0) {
								// let's assume new gpio level is "low"
								newLevel = 0;
								// if the current state/level is "high"?
								if ((cb.bit & level) != 0)
									newLevel = 1;
								//System.out.println("#3 "+changed+" : "+Integer.toBinaryString(cb.bit)+" : "+(cb.bit & changed));
								if ((cb.edge ^ newLevel) != 0)
									cb.alert(cb.gpio, newLevel, tick);

							}
						}
					} else
						// is it a watchdog message?
						if ((flags & PI_NTFY_FLAGS_WDOG) != 0) {
							gpio = flags & PI_NTFY_FLAGS_WDOG;
							for (GPIOListener cb : gpioListeners)
								if (cb.gpio == gpio)
									cb.alert(cb.gpio, PI_TIMEOUT, tick);
					}
				}

			} catch (IOException e) {
				// TODO: handle exception somehow :-)
				//throw new PigpioException("NotificationRouter.run",e);
            } catch (InterruptedException e) {
                // TODO: handle exception somehow :-)
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
	 * @param host The host name or ip address of the pigpio daemon.
	 * @param port The port of the pigpio daemon.
	 * @throws  PigpioException if not able to initialize/connect to pigpiod
	 */
	public PigpioSocket(String host, int port) throws PigpioException {
		this.host = host;
		this.port = port;
		gpioInitialize();
	}

	@Override
	public void gpioInitialize() throws PigpioException {
		try {
			if (slCmd == null)
				slCmd = new SocketLock(host, port);
			if (router == null) {
				router = new NotificationRouter(slCmd, host, port);
				router.start();
			}
		} catch (IOException|PigpioException e) {
			throw new PigpioException("gpioInitialize", e);
		}
	} // End of gpioInitialize()

	@Override
	public void reconnect() throws PigpioException {
		try {
		slCmd.reconnect();
		router.reconnect();
		} catch (IOException|PigpioException e) {
			throw new PigpioException("gpioReconnect", e);
		}

	}


	@Override
	public void gpioTerminate() throws PigpioException {
		try {
			// stop listener thread
			if (router != null) {
				router.terminate();
				router = null;
			}
			// stop command socket to pigpio
			if (slCmd != null) {
				slCmd.terminate();
				slCmd = null;
			}
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
			if (rc < 0)
				throw new PigpioException(rc);
			return rc != 0;
		} catch (IOException e) {
			throw new PigpioException("gpioRead", e);
		}
	}

	@Override
	public void gpioWrite(int pin, boolean value) throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WRITE, pin, value?1:0);
			if (rc < 0)
				throw new PigpioException(rc);
		} catch (IOException e) {
			throw new PigpioException("gpioWrite", e);
		}
	}

	// ############### NOTIFICATIONS

	@Override
	public int notifyOpen() throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_NO, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);
			return rc;
		} catch (IOException e) {
			throw new PigpioException("notifyOpen", e);
		}
	}

	@Override
	public void notifyBegin(int handle, int bits) throws PigpioException{
		try {
			int rc = slCmd.sendCmd(CMD_NB, handle, bits);
			if (rc < 0)
				throw new PigpioException(rc);
		} catch (IOException e) {
			throw new PigpioException("notifyBegin", e);
		}
	}

	@Override
	public void notifyPause(int handle) throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_NP, handle, 0);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("notifyPause", e);
		}

	}

	@Override
	public void notifyClose(int handle) throws PigpioException{
		try {
			int rc = slCmd.sendCmd(CMD_NC, handle, 0);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("notifyClose", e);
		}

	}

	@Override
	public void setWatchdog(int userGpio, int timeout) throws PigpioException{
		try {
			int rc =  slCmd.sendCmd(CMD_WDOG, userGpio, timeout);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("setWatchdog", e);
		}
	}

	// ################ WAVES

	@Override
	public void waveClear() throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVCLR, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);

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

        ByteBuffer bb;
        int rc;

		if (pulses == null || pulses.size() == 0)
			return 0;

		try {
            bb = ByteBuffer.allocate(pulses.size()*12);
            bb.order(ByteOrder.LITTLE_ENDIAN);
			for (Pulse p:pulses)
                bb.putInt(p.gpioOn).putInt(p.gpioOff).putInt(p.delay);

            rc = slCmd.sendCmd(CMD_WVAG,0,0,pulses.size()*12,bb.array());
			if (rc < 0)
				throw new PigpioException(rc);

			return rc;

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

		ByteBuffer bb;

		if (data.length == 0)
			return 0;

		try {
            bb = ByteBuffer.allocate(12);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.putInt(bbBits).putInt(bbStop).putInt(offset);
			bb.put(data);

			int rc = slCmd.sendCmd(CMD_WVAS, userGpio, baud, data.length + 12, bb.array());
			if (rc < 0)
				throw new PigpioException(rc);

			return rc;
		} catch (IOException e) {
			throw new PigpioException("waveAddSerial", e);
		}

	}


	@Override
	public void waveAddNew() throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVNEW, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("waveAddNew", e);
		}
	} // waveAddNew


	@Override
	public boolean waveTxBusy() throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVBSY, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);
			return rc != 0;
		} catch (IOException e) {
			throw new PigpioException("waveTxBusy", e);
		}
	} // waveTxBusy

	@Override
	public int waveTxStop() throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVHLT, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);
			return rc;
		} catch (IOException e) {
			throw new PigpioException("waveTxStop", e);
		}
	} // waveTxBusy

	@Override
	public int waveCreate() throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVCRE, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);
			return rc;
		} catch (IOException e) {
			throw new PigpioException("waveCreate", e);
		}
	}

	@Override
	public void waveDelete(int waveId) throws PigpioException{
		try {
			int rc = slCmd.sendCmd(CMD_WVDEL, 0, 0);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("waveDelete", e);
		}
	}

	@Override
	public int waveSendOnce(int waveId) throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVTX, waveId, 0);
			if (rc < 0)
				throw new PigpioException(rc);
			return rc;
		} catch (IOException e) {
			throw new PigpioException("waveSendOnce", e);
		}
	}

	@Override
	public int waveSendRepeat(int waveId) throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_WVTXR, waveId, 0);
			if (rc < 0)
				throw new PigpioException(rc);
			return rc;

		} catch (IOException e) {
			throw new PigpioException("waveSendRepeat", e);
		}
	}

	//############### I2C

	@Override
	public int i2cOpen(int i2cBus, int i2cAddr) throws PigpioException {
		try {
			return slCmd.sendCmd(CMD_I2CO, i2cBus, i2cAddr);
		} catch (IOException e) {
			throw new PigpioException("i2cOpen", e);
		}
	} // i2cOpen

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


	@Override
	public int i2cReadDevice(int handle, byte[] data) throws PigpioException {
		try {
			int rc = slCmd.sendCmd(CMD_I2CRD, handle, data.length);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
            slCmd.readBytes(data);

			return rc;
		} catch (IOException e) {
			throw new PigpioException("i2cReadDevice", e);
		}
	} // End of i2cReadDevice


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


	@Override
	public void gpioDelay(long delay) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_MILS, (int)delay, 0);
			return;
		} catch (IOException e) {
			throw new PigpioException("gpioDelay", e);
		}
	} // End of gpioDelay


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

	@Override
	public void setServoPulseWidth(int gpio, int pulseWidth) throws PigpioException {
		gpioServo(gpio, pulseWidth);
	}

	/**
	 * Not implemented
     */
	@Override
	public int getServoPulseWidth(int gpio) throws PigpioException {
		throw new NotImplementedException();
	}

	@Override
	public void gpioSetAlertFunc(int pin, Alert gpioAlert) throws PigpioException {
		router.addListener(new GPIOListener(pin, PI_EITHER_EDGE) {
			@Override
			public void alert(int gpio, int level, long tick) {
				gpioAlert.alert(gpio, level, tick);
			}
		});
	} // End of gpioSetAlertFunc


	@Override
	public void gpioTrigger(int gpio, long pulseLen, boolean level) throws PigpioException {
		try {

			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putInt(level?1:0);

			int rc = slCmd.sendCmd(CMD_TRIG, gpio, (int)pulseLen, 4, bb.array());
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("gpioTrigger");
		}

	}

	// ############### SPI
	
	@Override
	public int spiOpen(int spiChannel, int spiBaudRate, int flags) throws PigpioException {
		int rc = 0;
		try {
			ByteBuffer bb = ByteBuffer.allocate(4);
			bb.order(ByteOrder.LITTLE_ENDIAN);
			bb.putInt(flags);

			rc = slCmd.sendCmd(CMD_SPIO, spiChannel, spiBaudRate, 4, bb.array());
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("spiOpen failed",e);
		}

		return rc;
	}


	@Override
	public void spiClose(int handle) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_SPIC, handle, 0);
		} catch (IOException e) {
			throw new PigpioException("spiClose failed",e);
		}
	}


	@Override
	public int spiRead(int handle, byte[] data) throws PigpioException {
		int rc = 0;

		try {
			rc = slCmd.sendCmd(CMD_SPIR, handle, data.length);
			if (rc < 0)
				throw new PigpioException(rc);
			if (rc > 0)
				slCmd.readBytes(data);

			return rc;
		} catch (IOException e) {
			throw new PigpioException("spiRead failed", e);
		}
	}

	@Override
	public int spiWrite(int handle, byte[] data) throws PigpioException {
		int rc = 0;
		try {
			rc = slCmd.sendCmd(CMD_SPIW, handle, 0, data.length, data);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("spiWrite failed",e);
		}

		return rc;
	}


	@Override
	public int spiXfer(int handle, byte[] txData, byte[] rxData) throws PigpioException {
		int rc = 0;

		try {
			rc = slCmd.sendCmd(CMD_SPIX, handle, 0, txData.length, txData);
			if (rc > 0)
				slCmd.readBytes(rxData);
			else if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("spiXfer failed", e);
		}

		return rc;
	}

	// ######################## PWM

	@Override
	public void setPWMDutycycle(int gpio, int dutycycle) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_PWM, gpio, dutycycle);
		} catch (IOException e) {
			throw new PigpioException("setPWMDutycycle failed",e);
		}

	}

	@Override
	public int getPWMDutycycle(int gpio) throws PigpioException {
		int rc = 0;
		try {
			rc = slCmd.sendCmd(CMD_GDC, gpio, 0);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("getPWMDutycycle failed",e);
		}

		return rc;

	}

	@Override
	public void setPWMRange(int gpio, int range) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_PRS, gpio, range);
		} catch (IOException e) {
			throw new PigpioException("setPWMRange failed",e);
		}

	}

	@Override
	public int getPWMRange(int gpio) throws PigpioException {
		int rc = 0;
		try {
			rc = slCmd.sendCmd(CMD_PRG, gpio, 0);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("getPWMRange failed",e);
		}

		return rc;
	}

	@Override
	public int getPWMRealRange(int gpio) throws PigpioException {
		int rc = 0;
		try {
			rc = slCmd.sendCmd(CMD_PRRG, gpio, 0);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("getPWMRealRange failed",e);
		}

		return rc;
	}

	@Override
	public int setPWMFrequency(int gpio, int frequency) throws PigpioException {

		int rc = 0;
		try {
			rc = slCmd.sendCmd(CMD_PFS, gpio, frequency);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("setPWMFrequency failed",e);
		}

		return rc;

	}

	@Override
	public int getPWMFrequency(int gpio) throws PigpioException {
		int rc = 0;
		try {
			rc = slCmd.sendCmd(CMD_PFG, gpio, 0);
			if (rc < 0) {
				throw new PigpioException(rc);
			}
		} catch (IOException e) {
			throw new PigpioException("getPWMFrequency failed",e);
		}

		return rc;
	}


	// ################ SERIAL
	@Override
	public int serialOpen(String tty, int baudRate, int flags) throws PigpioException {
		int rc = 0;

		try {
			rc = slCmd.sendCmd(CMD_SERO, baudRate, flags, tty.length(), tty.getBytes());
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("serialOpen failed", e);
		}

		return rc;

	}

	@Override
	public void serialClose(int handle) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_SERC, handle, 0);
		} catch (IOException e) {
			throw new PigpioException("serialClose failed",e);
		}
	}

	@Override
	public byte serialReadByte(int handle) throws PigpioException {
		int rc = 0;

		try {
			rc = slCmd.sendCmd(CMD_SERRB, handle, 0);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("serialReadByte failed", e);
		}

		return (byte)rc;
	}

	@Override
	public void serialWriteByte(int handle, byte data) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_SERWB, handle, data);
		} catch (IOException e) {
			throw new PigpioException("serialWriteByte failed",e);
		}
	}

	@Override
	public byte[] serialRead(int handle, int count) throws PigpioException {
		byte[] data = new byte[1];
		int rc = 0;

		try {
			rc = slCmd.sendCmd(CMD_SERR, handle, count );
			if (rc < 0)
				throw new PigpioException(rc);
			if (rc > 0) {
				data = new byte[rc];
				slCmd.readBytes(data);
			}

		} catch (IOException e) {
			throw new PigpioException("serialRead failed", e);
		}

		return data;
	}

	@Override
	public void serialWrite(int handle, byte[] data) throws PigpioException {
		try {
			slCmd.sendCmd(CMD_SERW, handle, 0, data.length, data);
		} catch (IOException e) {
			throw new PigpioException("serialWrite failed",e);
		}
	}

	@Override
	public int serialDataAvailable(int handle) throws PigpioException {
		int rc = 0;

		try {
			rc = slCmd.sendCmd(CMD_SERDA, handle,0);
			if (rc < 0)
				throw new PigpioException(rc);

		} catch (IOException e) {
			throw new PigpioException("serialDataAvailable failed", e);
		}

		return rc;
	}

	// ########################

	/**
	 * Not implemented
	 */
	@Override
	public void setDebug(boolean flag) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
		
	}

	/**
	 * Not implemented
	 */
	@Override
	public long gpioxPulseAndWait(int outGpio, int inGpio, long waitDuration, long pulseHoldDuration, boolean pulseLow) throws PigpioException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void addCallback(GPIOListener gpioListener) throws PigpioException{
		this.router.addListener(gpioListener);
	}

	@Override
	public void removeCallback(GPIOListener gpioListener) throws PigpioException{
		this.router.removeListener(gpioListener);
	}


} // End of class
// End of file