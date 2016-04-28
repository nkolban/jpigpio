package tests;

import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.packet.Protocol;
import jpigpio.packet.Tx;

import java.util.Arrays;

/**
 * Created by Jozef on 26.04.2016.
 */
public class Test_PacketTx {

    // GPIO pin for transmitting
    static int GPIO_TX = 26;

    // pigpiod host & port
    static String host = "pigpiod-host";
    static int port = 8888;

    // protocol defines message length, repetition of messages, signal levels etc.
    static Protocol protocol = new Protocol();

    static PigpioSocket pi;
    static Tx tx;

    public static void main(String[] args) throws PigpioException, InterruptedException {

        // test message
        byte[] TX_TEST_MSG = new byte[] {0,0,0,1,15,1,5,10,12,2};
        byte[] TX_TEST_MSG2 = new byte[] {1,0,0,1,0,0,0,0,0,2};

        int waitStep = 2000;
        int w = 0;

        // gpigpiod instance
        pi = new PigpioSocket(host,port);
        pi.gpioInitialize();

        tx = new Tx(pi, GPIO_TX, protocol);

        System.out.println("Transmit test message sending "+ Arrays.toString(TX_TEST_MSG) + " " + protocol.DGRM_REPEAT_TX + " times");
        tx.put(TX_TEST_MSG);

        System.out.println("Transmit test message sending "+ Arrays.toString(TX_TEST_MSG2) + " " + protocol.DGRM_REPEAT_TX + " times");
        tx.put(TX_TEST_MSG2);

        while (!tx.ready()) {
            System.out.println("Waiting for transmitter to finish its job. "+w+"ms");
            Thread.sleep(waitStep);
            w += waitStep;
        }

        System.out.println("Terminating transmitter.");
        tx.terminate();

        System.out.println("Terminating RPi connection.");
        pi.gpioTerminate();

    }

}
