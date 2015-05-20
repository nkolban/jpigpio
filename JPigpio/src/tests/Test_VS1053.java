package tests;

import java.io.File;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;
import jpigpio.devices.VS1053;

public class Test_VS1053 {
	private int XRESET = JPigpio.PI_GPIO20;
	private int DREQ = JPigpio.PI_GPIO21;
	
	private VS1053 vs1053;
	
	public static void main(String args[]) {
		System.out.println("Test_VS1053");
		Test_VS1053 app = new Test_VS1053();
		app.run();
	}

	public void run() {

		try {
			//JPigpio pigpio = new PigpioSocket("raspi", 8888);
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
//			pigpio.gpioSetMode(XCS, JPigpio.PI_OUTPUT);
//			pigpio.gpioSetMode(XDCS, JPigpio.PI_OUTPUT);
			pigpio.gpioSetMode(DREQ, JPigpio.PI_INPUT);
			pigpio.gpioSetMode(XRESET, JPigpio.PI_OUTPUT);
			
			// The XCS and XDCS are used to enable the SPI modes of the device.  When XCS is low, the device
			// will accept control commands.  When XDCS is low, the device will accept data input for playing.
			// It isn't known what happens if both should be low so don't try it.
//			pigpio.gpioWrite(XDCS, true);
//			pigpio.gpioWrite(XCS, false);
			pigpio.gpioWrite(XRESET, true);
			
			Utils.addShutdown(pigpio);
			
			vs1053 = new VS1053(pigpio, DREQ);
			pigpio.gpioWrite(XRESET, false);
			pigpio.gpioDelay(5000);
			pigpio.gpioWrite(XRESET, true);
			vs1053.disableMidi();
			vs1053.setClockF(0x8800);
			//dump();

			//vs1053.softReset();
			vs1053.setVolume(0x30);
//			pigpio.gpioWrite(XRESET, false);
//			pigpio.gpioDelay(5000);
//			pigpio.gpioWrite(XRESET, true);
			//vs1053.setVolume(0x00);
			//vs1053.setLine(false);

//			vs1053.setTestMode(true);
			vs1053.dump();
//			pigpio.gpioDelay(5000);
//			System.out.println("Sine test starting ...");
//			vs1053.startSineTest();
//			pigpio.gpioDelay(30, JPigpio.PI_SECONDS);
//			System.out.println("Tests ending ...");
//			
//			//vs1053.memoryTest();
//			vs1053.setTestMode(false);
			//vs1053.playFile(new File("/mnt/share/Data/Audio/sample1.wav"));
			//vs1053.playFile(new File("/mnt/share/Data/Audio/sample1-96k.ogg"));
			vs1053.playFile(new File("/mnt/share/Data/Audio/sample.mp3"));
			//pigpio.gpioDelay(5000);
			pigpio.gpioTerminate();
			
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	}
	
//	public void dump() throws PigpioException {
//		int mode = vs1053.getMode();
//		System.out.println("Mode: " + Utils.int16ToBinary(mode));
//		System.out.println("Mode: " + vs1053.format(mode, "MODE"));
//		System.out.println("Status: " + Utils.int16ToBinary(vs1053.getStatus()));
//		
//		int audata = vs1053.getAudata();
//		System.out.println("Audata: " + Utils.int16ToBinary(audata));
//		System.out.println("Audata: " + vs1053.format(audata, "AUDATA"));
//		System.out.println("Volume: " + Utils.int16ToBinary(vs1053.getVolume()));
//		System.out.println("Bass: " + Utils.int16ToBinary(vs1053.getBass()));
//		System.out.println("---");
//	}
} // End of class
// End of file