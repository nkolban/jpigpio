package tests;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.Utils;

/**
 * Simple test class to echo input to output.
 * To allow automated RX-TX testing, then if no input is received, then test sends test message
 *
 * Created by Jozef on 12.06.2016.
 */

public class Test_Serial {
    public static void main(String args[]) {
        System.out.println("Test_Serial");
        Test_Serial app = new Test_Serial();
        app.run();
    }

    public void run() {

        String host = "pigpiod-host";
        String serialPort = "/dev/ttyAMA0";
        int baudRate = 9600;

        int handle;
        int avail = 0;
        byte[] data;
        long startTime = System.currentTimeMillis();
        int counter = 0;
        int testC = 0;
        String rcvd;

        try {
            System.out.println("Opening Pigpio.");
            JPigpio pigpio = new PigpioSocket(host, 8888);
            //JPigpio pigpio = new Pigpio();

            pigpio.gpioInitialize();
            Utils.addShutdown(pigpio);

            System.out.println("Opening serial port "+serialPort);
            handle = pigpio.serialOpen(serialPort, baudRate, 0);

            System.out.println("Going to echo all input for the next 20 seconds");
            while (System.currentTimeMillis() - startTime < 20000){

                Thread.sleep(500);
                avail = pigpio.serialDataAvailable(handle);
                if (avail > 0){
                    data = pigpio.serialRead(handle, avail);
                    rcvd = new String(data);
                    System.out.println("RECEIVED: "+rcvd );

                    // do not echo input starting with ECHO to prevent feedback loop :-)
                    if (rcvd.indexOf("ECHO") != 0) {
                        pigpio.serialWrite(handle, "ECHO:".getBytes());
                        pigpio.serialWrite(handle, data);
                        System.out.println("ECHO containing same data sent back");
                    }

                    counter = 0;
                } else if (++counter > 5) {
                    pigpio.serialWrite(handle, ("TEST "+(++testC)).getBytes());
                }
            }

            pigpio.serialClose(handle);
            pigpio.gpioTerminate();

        } catch (PigpioException|InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Test Serial Completed.");
    }
}

