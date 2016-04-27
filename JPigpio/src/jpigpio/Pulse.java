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
     * binary encoded GPIO number {@code (1<<gpioOn)} to switch on at the start of the pulse.
     * If zero, then no GPIO will be switched on.
     * @param gpioOff
     * binary encoded GPIO number {@code (1<<gpioOff)} to switch off at the start of the pulse.
     * If zero, then no GPIO will be switched off.
     * @param delay
     *          the delay in microseconds before the next pulse.
     */
    public Pulse(int gpioOn, int gpioOff, int delay){
        this.gpioOn = gpioOn;
        this.gpioOff = gpioOff;
        this.delay = delay;
    }
}
