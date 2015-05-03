package tests;

import jpigpio.JPigpio;
import jpigpio.Pigpio;
import jpigpio.PigpioException;
import jpigpio.devices.Servo;
import jpigpio.sensors.WiiNunchuck;

public class Test_NunchuckServos {
	
	private final int yawServoGpio=17;
	private final int pitchServoGpio=18;
	private int yawValue = 128;
	private int pitchValue = 128;
	private int delta = 5;

	public static void main(String[] args) {
		Test_NunchuckServos app = new Test_NunchuckServos();
		app.run();
	}

	public void run() {
		try {
			JPigpio pigpio = new Pigpio();
			pigpio.gpioInitialize();
			pigpio.gpioSetMode(yawServoGpio, Pigpio.PI_OFF);
			WiiNunchuck wiiNunchuck = new WiiNunchuck(pigpio);
			wiiNunchuck.initialize();
			
			Servo yawServo = new Servo(pigpio, yawServoGpio, 0, 255);
			Servo pitchServo = new Servo(pigpio, pitchServoGpio, 0, 255);

			while (true) {
				wiiNunchuck.readValue();
				System.out.println("Nunchuck: " + wiiNunchuck.toString());
				int yawChange = Byte.toUnsignedInt(wiiNunchuck.getJoyX());
				int pitchChange = Byte.toUnsignedInt(wiiNunchuck.getJoyY());
				//System.out.println("Servo val = " + val);
				if (yawChange < 64) {
					decreateYaw();
				}
				if (yawChange > 128 + 64) {
					increaseYaw();
				}
				if (pitchChange < 64) {
					decreasePitch();
				}
				if (pitchChange > 128 + 64) {
					increasePitch();
				}
				yawServo.setValue(yawValue);
				pitchServo.setValue(pitchValue);
				pigpio.gpioDelay(40000);
			} // End of while true
		} catch (PigpioException e) {
			e.printStackTrace();
		}
	} // End of run
	
	private void increaseYaw() {
		if (yawValue < 255-delta) {
			yawValue+=delta;
		}
	}
	
	private void decreateYaw() {
		if (yawValue > delta) {
			yawValue-=delta;
		}
	}
	
	private void decreasePitch() {
		if (pitchValue < 255 - delta) {
			pitchValue+=delta;
		}
	}
	
	private void increasePitch() {
		if (pitchValue > delta) {
			pitchValue-=delta;
		}
	}
} // End of Test_Nunchuck2
// End of file