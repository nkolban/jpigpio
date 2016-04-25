package tests;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.packet.*;

import java.util.Arrays;


/**
 * Created by Jozef on 24.04.2016.
 */
public class Test_PacketTxRx {

    public static void main(String[] args) throws PigpioException, InterruptedException {

        int RX = 24;    // GPIO for receiving
        int TX = 26;    // GPIO for transmitting

        // test message
        byte[] TX_TEST_MSG = new byte[] {0,0,0,1,15,1,5,10,12,2};
        byte[] TX_TEST_MSG2 = new byte[] {1,2,3,4,5,6,7,8,9,10};

        int waitForData = 10000; // milliseconds to wait for packets
        int waitStep = 2000;

        // protocol defines message length, repetition of messages, signal levels etc.
        Protocol protocol = new Protocol();

        // gpigpiod instance
        JPigpio pi = new PigpioSocket("192.168.1.33",8888);
        pi.gpioInitialize();

        Tx tx = new Tx(pi, TX, protocol);
        Rx rx = new Rx(pi, RX, protocol);

        //Thread.sleep(3000);

        System.out.println("Transmit test message sending "+ Arrays.toString(TX_TEST_MSG) + " " + protocol.TX_REPEAT + " times");
        tx.put(TX_TEST_MSG);
        //Thread.sleep(1000);
        System.out.println("Transmit test message sending "+ Arrays.toString(TX_TEST_MSG2) + " " + protocol.TX_REPEAT + " times");
        tx.put(TX_TEST_MSG2);
        //Thread.sleep(1000);

        System.out.println("Waiting "+ waitForData+ " ms for data.");

        int w = waitForData;
        while (w > 0){
            while (rx.available() > 0)
                System.out.println("Received "+Arrays.toString(rx.get()));
            Thread.sleep(waitStep);
            w -= waitStep;
            System.out.println("Waiting "+ w + " ms more.");
        }

        System.out.println("Terminating receiver.");
        rx.terminate();
        System.out.println("Terminating transmitter.");
        tx.terminate();
        System.out.println("Terminating RPi connection.");
        pi.gpioTerminate();

    }

}
