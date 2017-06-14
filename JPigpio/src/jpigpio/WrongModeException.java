package jpigpio;

/**
 * An exception that states that a pin is in the wrong mode.
 *
 */
public class WrongModeException extends PigpioException {

	/**
	 * The pin that is flagged as being in the wrong mode.
	 */
	private int pin;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1268207535639503129L;
	
	public WrongModeException(int pin) {
		super();
		this.pin = pin;
	} // End of constructor
	
	/**
	 * Retrieve the pin that was flagged as being in the wrong mode.
	 * @return gpio number
	 */
	public int getPin() {
		return pin;
	} // End of getPin
	
	@Override
	public String toString() {
		return super.toString() + ": " + pin;
	} // End of toString
} // End of class
// End of file