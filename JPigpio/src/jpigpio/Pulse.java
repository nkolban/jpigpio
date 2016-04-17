package jpigpio;

/**
 * Class to store pulse information
 */
public class Pulse {
    int gpioOn;
    int gpioOff;
    int delay;

    /**
     *       Initialises a pulse.
     *
     * @param gpioOn
     *          the GPIO to switch on at the start of the pulse.
     * @param gpioOff
     *          the GPIO to switch off at the start of the pulse.
     * @param delay
     *          the delay in microseconds before the next pulse.
     */
    public Pulse(int gpioOn, int gpioOff, int delay){
        this.gpioOn = gpioOn;
        this.gpioOff = gpioOff;
        this.delay = delay;
    }
}
