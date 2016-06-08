package tests;

import jpigpio.*;

/**
 * Created by Jozef on 20.04.2016.
 */
public class Test_SocketListen {

    class MyNotificationListener extends NotificationListener {

        MyNotificationListener(int userGpio, int edge){
            super(userGpio, edge);
        }

        @Override
        public void processNotification(int gpio, int level, long tick){
            this.count++;
            System.out.println("MyNotificationListener GPIO="+gpio);
        }

    }

    public static void main(String args[]) {
        System.out.println("Test_SocketListen");
        Test_SocketListen app = new Test_SocketListen();
        app.run();
    }

    public void run() {

        String host = "pigpiod-host";
        int gpio = 21;

        NotificationListener cb = new MyNotificationListener(gpio,JPigpio.PI_RISING_EDGE);
        try {
            JPigpio pigpio = new PigpioSocket(host, 8888);
            pigpio.gpioInitialize();
            Utils.addShutdown(pigpio);
            pigpio.addCallback(cb);
            System.out.println("Listening for changes on GPIO "+gpio);
            while(true) {
                System.out.println(cb.countCalled());
                Thread.sleep(1000);
            }

        } catch (PigpioException|InterruptedException e) {
            e.printStackTrace();
        }
    } // End of run
}
