package tests;

import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.Utils;
import jpigpio.packet.Protocol;
import jpigpio.packet.Rf433tx;

import java.util.Arrays;

// Created by Jozef on 26.04.2016.

/**
 * Simple example of using 433MHz transmitter (e.g. XD-RF-5V) with Java and Pigpiod.
 */
public class Test_Rf433Tx {

    // GPIO pin for transmitting
    static int GPIO_TX = 27;

    // pigpiod host & port
    static String host = "pigpiod-host";
    static int port = 8888;

    // protocol defines message length, repetition of messages, signal levels etc.
    static Protocol protocol = new Protocol();

    static PigpioSocket pi;
    static Rf433tx rf433tx;

    public static void main(String[] args) throws PigpioException, InterruptedException {

        // test message
        byte[] TX_TEST_MSG = new byte[]  {0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x11, 0x12, 0x13, 0x14, 0x15};
        byte[] TX_TEST_MSG2 = new byte[] {(byte)0xA1, (byte)0xA2, (byte)0xA3, (byte)0xA4, (byte)0xA5, (byte)0xA6, (byte)0xA7,
                (byte)0xF1, (byte)0xF2, (byte)0xF3, (byte)0xF4, (byte)0xF5, (byte)0xF6, (byte)0xF7, (byte)0xF8, (byte)0xF7};

        int waitStep = 2000;
        int w = 0;

        protocol.setRepeatCount(15);
        protocol.setDataSize(16);

        // gpigpiod instance
        pi = new PigpioSocket(host,port);
        pi.gpioInitialize();

        rf433tx = new Rf433tx(pi, GPIO_TX, protocol);

        System.out.println("Transmit test message sending "+ Utils.bytesToHex(TX_TEST_MSG) + " " + protocol.DGRM_REPEAT_TX + " times");
        rf433tx.put(TX_TEST_MSG);

        System.out.println("Transmit test message sending "+ Utils.bytesToHex(TX_TEST_MSG2) + " " + protocol.DGRM_REPEAT_TX + " times");
        rf433tx.put(TX_TEST_MSG2);

        while (!rf433tx.ready()) {
            System.out.println("Waiting for transmitter to finish its job. "+w+"ms");
            Thread.sleep(waitStep);
            w += waitStep;
        }

        System.out.println("Terminating transmitter.");
        rf433tx.terminate();

        System.out.println("Terminating RPi connection.");
        pi.gpioTerminate();

    }

}
