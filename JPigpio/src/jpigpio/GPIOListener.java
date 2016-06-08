package jpigpio;

/**
 * Simple interface for implementing GPIO callbacks
 */
public abstract class GPIOListener {

    public int bit;   // bit-map representing respective GPIOs
    public int edge;
    public int gpio;

    public GPIOListener(int gpio, int edge){
        this.gpio = gpio;
        this.bit = 1 << this.gpio;
        this.edge = edge;
    }

    abstract public void processNotification(int gpio, int level, long tick);

}
