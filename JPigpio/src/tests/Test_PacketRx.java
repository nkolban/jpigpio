package tests;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.packet.*;

import java.util.Arrays;


/**
 * Created by Jozef on 24.04.2016.
 */
public class Test_PacketRx {

    public static void main(String[] args) throws PigpioException, InterruptedException {

        int RX = 24;    // GPIO for receiving

        // pigpiod host & port
        String host = "pigpiod-host";
        int port = 8888;

        int waitForData = 10000; // milliseconds to wait for packets
        int waitStep = 2000;

        // protocol defines message length, repetition of messages, signal levels etc.
        Protocol protocol = new Protocol();

        // connect to gpigpiod instance
        JPigpio pi = new PigpioSocket(host,port);
        pi.gpioInitialize();

        Rx rx = new Rx(pi, RX, protocol);

        System.out.println("Waiting "+ waitForData+ " ms for data.");

        int w = waitForData;
        while (w > 0){
            while (rx.available() > 0)
                System.out.println("Received "+Arrays.toString(rx.get()));
            Thread.sleep(waitStep);
            w -= waitStep;
            System.out.println("Waiting "+ w + " ms more.");
        }

        System.out.println("RX Byte Errors = "+rx.byteErrorCount());
        System.out.println("RX Datagram Errors = "+rx.datagramErrorCount());
        System.out.println("Terminating receiver.");
        rx.terminate();

        System.out.println("Terminating RPi connection.");
        pi.gpioTerminate();

    }

}
