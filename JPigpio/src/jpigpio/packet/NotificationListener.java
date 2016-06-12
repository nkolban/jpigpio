package jpigpio.packet;

import jpigpio.GPIOListener;

/**
 * NotificationListener is object, which after being registered with NotificationRouter (see class PigpioSocket)
 * receives notifications coming from Pigpiod. Every relevant notification (NotificationRouter filters out those
 * notifications irrelevant for specific NotificationListener object based on EDGE and GPIO pin which listener
 * is registered for) triggers calling method alert.
 */
public class NotificationListener extends GPIOListener {

    protected int count = 0;
    protected boolean reset = false;

    protected int byteErrorCount = 0;     //byte error count
    protected int datagramErrorCount = 0;


    public NotificationListener(int userGpio, int edge){
        super(userGpio, edge);
    }

    public void alert(int gpio, int level, long tick){
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
