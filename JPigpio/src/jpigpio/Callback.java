package jpigpio;

import static jpigpio.JPigpio.PI_RISING_EDGE;

/**
 * Created by Jozef on 17.04.2016.
 */
public class Callback {

    int bit = 0; // bit-map representing respective GPIOs
    int edge = PI_RISING_EDGE;
    int gpio = -1;

    int count = 0;
    boolean reset = false;


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
