package tests;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.Utils;
import jpigpio.packet.*;


/**
 * Simple example of using 433MHz transmitter (e.g. FS1000A) with Java and Pigpiod.
 */
public class Test_Rf433Rx {

    public static void main(String[] args) throws PigpioException, InterruptedException {

        int GPIO_RX = 17;    // GPIO for receiving

        // pigpiod host & port
        String host = "pigpiod-host";
        int port = 8888;

        int waitForData = 20000; // milliseconds to wait for packets
        int waitStep = 1000;

        // protocol defines message length, repetition of messages, signal levels etc.
        Protocol protocol = new Protocol();

        protocol.setRepeatCount(5);
        protocol.setRxRepeatCount(1); // setting this to lower than TX repeat count value means duplicates will be reported
        protocol.setDataSize(16);

        // connect to gpigpiod instance
        JPigpio pigpio = new PigpioSocket(host,port);
        //pigpio.gpioInitialize();

        Rf433rx rf433rx = new Rf433rx(pigpio, GPIO_RX, protocol);

        System.out.println("Waiting "+ waitForData+ " ms for data.");

        int w = waitForData;
        while (w > 0){
            while (rf433rx.available() > 0)
                System.out.println("Received "+ Utils.bytesToHex(rf433rx.get()));
            Thread.sleep(waitStep);
            w -= waitStep;
            System.out.println("Waiting "+ w + " ms more.");
        }

        System.out.println("RX Byte Errors = "+ rf433rx.byteErrorCount());
        System.out.println("RX Datagram Errors = "+ rf433rx.datagramErrorCount());
        System.out.println("Terminating receiver.");
        rf433rx.terminate();

        System.out.println("Terminating RPi connection.");
        pigpio.gpioTerminate();

    }

}
