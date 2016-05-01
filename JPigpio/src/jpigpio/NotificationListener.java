package jpigpio;

import static jpigpio.JPigpio.PI_RISING_EDGE;

/**
 * NotificationListener is object, which after being registered with NotificationRouter (see class PigpioSocket)
 * receives notifications coming from Pigpiod. Every relevant notification (NotificationRouter filters out those
 * notifications irrelevant for specific NotificationListener object based on EDGE and GPIO pin which listener
 * is registered for) triggers calling method processNotification.
 */
public class NotificationListener {

    protected int bit = 0; // bit-map representing respective GPIOs
    protected int edge = PI_RISING_EDGE;
    protected int gpio = -1;

    protected int count = 0;
    protected boolean reset = false;

    protected int byteErrorCount = 0;     //byte error count
    protected int datagramErrorCount = 0;


    public NotificationListener(int userGpio, int edge){
        this.gpio = userGpio;
        this.edge = edge;
        this.bit = 1 << userGpio;
    }

    public void processNotification(int gpio, int level, long tick){
        count++;
    }

    public int countCalled(){
        return count;
    }

    public void countReset(){
        count = 0;
    }

    public int byteErrorCount(){
        return byteErrorCount;
    }

    public int datagramErrorCount(){
        return datagramErrorCount;
    }

}
