package jpigpio;

/**
 * Simple interface for implementing GPIO callbacks
 */
public abstract class GPIOListener implements Alert{

    // PI_RISING_EDGE  = 0
    // PI_FALLING_EDGE = 1
    // PI_EITHER_EDGE  = 2

    public int bit;   // bit-map representing respective GPIOs
    public int edge;
    public int gpio;

    public GPIOListener(int gpio, int edge){
        this.gpio = gpio;
        this.bit = 1 << this.gpio;
        this.edge = edge;
    }

}
