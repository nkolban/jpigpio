package tests;

import jpigpio.*;

/**
 * Simple example showing how to process GPIO alerts/notifications via socket interface
 * <br><br>
 * Created by Jozef on 20.04.2016.
 */
public class Test_SocketListen {

    /**
     * This would be your class implementing alert method processing notifications
     * received from pigpiod
     */
    class MyListener extends GPIOListener {

        public int count = 0;

        MyListener(int userGpio, int edge){
            super(userGpio, edge);
        }

        @Override
        public void alert(int gpio, int level, long tick){
            this.count++;
            System.out.println("MyListener #"+count+" GPIO="+gpio);
        }

    }

    public static void main(String args[]) {
        System.out.println("Test_SocketListen");
        Test_SocketListen app = new Test_SocketListen();
        app.run();
    }

    public void run() {

        String host = "pigpiod-host";

        int gpio1 = 23;
        int gpio2 = 24;
        int gpio3 = 25;

        long startTime = System.currentTimeMillis();

        try {
            JPigpio pigpio = new PigpioSocket(host, 8888);
            pigpio.gpioInitialize();

            // receive notifications for gpio
            pigpio.addCallback(new MyListener(gpio1,JPigpio.PI_RISING_EDGE) );
            System.out.println("Listening for changes on GPIO "+gpio1);

            // receive notifications for gpio
            pigpio.addCallback(new MyListener(gpio2,JPigpio.PI_FALLING_EDGE) );
            System.out.println("Listening for changes on GPIO "+gpio2);

            // alternative approach using lambda expression
            pigpio.gpioSetAlertFunc(gpio3, (int gpio, int level, long tick) ->
                    {
                        System.out.println("ALERT Received:  gpio="+gpio+"  level="+Integer.toBinaryString(level)+"  tick="+tick);
                    });

            System.out.println("Waiting 20s for incoming notifications");
            while(System.currentTimeMillis() - startTime < 20000) {

                Thread.sleep(100);
            }

            System.out.println("Finished.");

            pigpio.gpioTerminate();

        } catch (PigpioException|InterruptedException e) {
            e.printStackTrace();
        }

    } // End of run
}
