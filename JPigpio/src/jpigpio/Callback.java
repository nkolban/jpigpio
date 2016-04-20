package jpigpio;

import static jpigpio.JPigpio.PI_RISING_EDGE;

/**
 * Created by Jozef on 17.04.2016.
 */
public class Callback {

    protected int bit = 0; // bit-map representing respective GPIOs
    protected int edge = PI_RISING_EDGE;
    protected int gpio = -1;

    protected int count = 0;
    protected boolean reset = false;


    public Callback(int userGpio, int edge){
        this.gpio = userGpio;
        this.edge = edge;
        this.bit = 1 << userGpio;
    }

    public void func(int gpio, int level, long tick){
        count++;
    }

    public int countCalled(){
        return count;
    }

    public void countReset(){
        count = 0;
    }

}
