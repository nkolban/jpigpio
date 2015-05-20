package jpigpio;

/**
 * An exception that states that a pin is in the wrong mode.
 *
 */
public class WrongModeException extends PigpioException {

	private int pin;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1268207535639503129L;
	
	public WrongModeException(int pin) {
		super();
		this.pin = pin;
	}
	
	public int getPin() {
		return pin;
	}
	
	@Override
	public String toString() {
		return super.toString() + ": " + pin;
	}
} // End of class
// End of file