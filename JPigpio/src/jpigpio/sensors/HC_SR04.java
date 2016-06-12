package jpigpio.sensors;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.WrongModeException;

public class HC_SR04 {
	private JPigpio pigpio;
	private int triggerGpio;
	private int echoGpio;
	/**
	 * Construct the object for this class.
	 * @param pigpio The reference to the Pigpio controller
	 */
	public HC_SR04(JPigpio pigpio, int triggerGpio, int echoGpio) throws PigpioException {
		this.pigpio = pigpio;
		this.triggerGpio = triggerGpio;
		this.echoGpio = echoGpio;
		if (pigpio.gpioGetMode(triggerGpio) != JPigpio.PI_OUTPUT) {
			throw new WrongModeException(triggerGpio);
		}
		if (pigpio.gpioGetMode(echoGpio) != JPigpio.PI_INPUT) {
			throw new WrongModeException(echoGpio);
		}
	} // End of constructor
	
	/**
	 * Get the delay in microseconds for a trigger/echo
	 * @return The time in microseconds between a trigger and an echo response
	 * @throws PigpioException on pigpiod error
	 */
	public long getDelay() throws PigpioException {
		long delay = pigpio.gpioxPulseAndWait(triggerGpio, echoGpio, 50000, 10, false);
		return delay;
	} // End of getDelay
	
	
	/**
	 * Get the sensor measured distance to the target in meters.
	 * @return The distance to the target in meters or -1 if not determined.
	 * @throws PigpioException on pigpiod error
	 */
	public double getMetricDistance() throws PigpioException {
		long delay = getDelay();
		if (delay == -1) {
			return -1.0;
		}
		return delay / 1000000.0 * 340.39 / 2.0;
	} // End of getMetricDistance 
	
	/**
	 * Get the distance in inches of detection or -1 if no object detected.
	 * @return The distance in inches to object.
	 * @throws PigpioException on pigpiod error
	 */
	public double getImperialDistance() throws PigpioException {
		double metricDistance = getMetricDistance();
		if (metricDistance == -1.0) {
			return -1.0;
		}
		// Convert meters to inches
		return metricDistance * 39.3701;
	} // End of getImperialDistance
	
} // End of class HC_SR04
// End of file