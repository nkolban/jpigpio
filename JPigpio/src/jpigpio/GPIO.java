package jpigpio;

public class GPIO {
	private JPigpio pigpio;
	private int pin;
	
	public GPIO(JPigpio pigpio, int pin) {
		assert pigpio != null;
		
		this.pigpio = pigpio;
		this.pin = pin;
	}
	
	
	public GPIO(JPigpio pigpio, int pin, int direction) throws PigpioException {
		this(pigpio, pin);
		setDirection(direction);
	}
	
	public int getDirection() throws PigpioException {
		return pigpio.gpioGetMode(pin);
	}
	
	public void setDirection(int direction) throws PigpioException {
		pigpio.gpioSetMode(pin, direction);
	}
	
	public void setValue(boolean value) throws PigpioException {
		pigpio.gpioWrite(pin, value);
	}
	
	public void setValue(int value) throws PigpioException {
		if (value !=0 && value != 1) {
			throw new BadValueException();
		}
		pigpio.gpioWrite(pin, value==1);
	}
	
	public boolean getValue() throws PigpioException {
		return pigpio.gpioRead(pin);
	}
	
	public int getPin() {
		return pin;
	}

    public void setAlert(Alert alert) throws PigpioException {
        pigpio.gpioSetAlertFunc(getPin(), alert);
    }

    public void setPulldown(int pulldown) throws PigpioException {
        pigpio.gpioSetPullUpDown(getPin(), pulldown);
    }

    public void delay(long delay, int type) throws PigpioException {
        pigpio.gpioDelay(delay, type);
    }

    /**
     *
     * Should only be needed if not mapped by gpio class
     *
     * @return Pigpio instance
     */
    public JPigpio getPigpio() {
        return pigpio;
    }
} // End of class
// End of file
