package jpigpio.devices;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.WrongModeException;

/**
 * Control a stepper motor
 *
 */
public class Stepper {
	private JPigpio pigpio;
	
	private int blueGpio; // IN1
	private int pinkGpio; // IN2
	private int yellowGpio; // IN3
	private int orangeGpio; // IN4

	// The step counter state
	private int step = 0;
	
	// Step 0 - Blue/Pink
	// Step 1 - Pink/Yellow
	// Step 2 - Yellow/Orange
	// Step 3 - Orange/Blue
	
	// The step data for a full step, high torque
	private int stepData[][] = {{1,1,0,0},{0,1,1,0},{0,0,1,1}, {1,0,0,1}};
	
	/**
	 * Create an instance of the Stepper object used to control the stepper motor.
	 * @param pigpio The pigpio controller
	 * @param blueGpio The blue gpio pin (IN1)
	 * @param pinkGpio The pink gpio pin (IN2)
	 * @param yellowGpio The yellow gpio pin (IN3)
	 * @param orangeGpio The orange gpio pin (IN4)
	 * @throws PigpioException
	 */
	public Stepper(JPigpio pigpio, int blueGpio, int pinkGpio, int yellowGpio, int orangeGpio) throws PigpioException {
		this.pigpio = pigpio;
		this.blueGpio = blueGpio; // IN1
		this.pinkGpio = pinkGpio; // IN2
		this.yellowGpio = yellowGpio; // IN3
		this.orangeGpio = orangeGpio; // IN4
		
		// Check that the pins have the correct modes.
		if (pigpio.gpioGetMode(blueGpio) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(blueGpio);
		}
		if (pigpio.gpioGetMode(pinkGpio) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(pinkGpio);
		}
		if (pigpio.gpioGetMode(yellowGpio) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(yellowGpio);
		}
		if (pigpio.gpioGetMode(orangeGpio) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(orangeGpio);
		}
		
		// Set the initial state of the motor.
		setData();
	} // End of constructor
	
	/**
	 * Set the GPIO pins as a function of our current step state.
	 * 0 = 1100
	 * 1 = 0110
	 * 2 = 0011
	 * 3 = 1001
	 * @throws PigpioException
	 */
	private void setData() throws PigpioException {
		pigpio.gpioWrite(blueGpio, stepData[step][0] != 0);
		pigpio.gpioWrite(pinkGpio, stepData[step][1] != 0);
		pigpio.gpioWrite(yellowGpio, stepData[step][2] != 0);
		pigpio.gpioWrite(orangeGpio, stepData[step][3] != 0);
	} // End of setData
	
	/**
	 * Move the stepper forward one step.
	 * @throws PigpioException
	 */
	public void forward() throws PigpioException{
		step = (step+1)%4;
		setData();
	} // End of forward
	
	/**
	 * Move the stepper backwards one step.
	 * @throws PigpioException
	 */
	public void backward() throws PigpioException {
		step = (step-1)%4;
		setData();
	} // End of backward
} // End of class
// End of file