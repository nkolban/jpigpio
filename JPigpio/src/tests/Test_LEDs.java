package tests;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.PigpioSocket;
import jpigpio.Utils;

/**
 * Example showing how to operate 16 LEDs using two shift-out registers 74HC595 using Raspberry Pi.
 * It can be easily adapted to operate 7 segment LED displays.
 *
 * Idea has been taken from https://www.arduino.cc/en/Tutorial/ShiftOut
 *
 * Created by Jozef on 17.06.2016.
 */
public class Test_LEDs {

    JPigpio pigpio;

    public static void main(String args[]) {
        System.out.println("Test_LEDs");
        Test_LEDs app = new Test_LEDs();
        app.run();
    }

    /**
     * Low level function sending data to two 74HC595 shift registers connected in serial
     * effectively displaying 16 bits/outputs
     * @param dataPin GPIO pin to which 'data' (DS pin of 74HC595) is connected.
     * @param clockPin GPIO pin to which 'clock' (SH_CP pin of 74HC595) is connected
     * @param data 8 bit data to be displayed
     * @throws PigpioException if write is not successful
     */
    void shiftOut(int dataPin, int clockPin, int data) throws PigpioException{

        boolean pinState;
        byte dataOut = (byte)(data & 0xFF);

        pigpio.gpioSetMode(clockPin, JPigpio.PI_OUTPUT);
        pigpio.gpioSetMode(dataPin, JPigpio.PI_OUTPUT);

        //clear everything out just in case to
        //prepare shift register for bit shifting
        pigpio.gpioWrite(dataPin, false);
        pigpio.gpioWrite(clockPin, false);

        for (int i = 7; i >= 0; i--) {
            pigpio.gpioWrite(clockPin, false);

            pinState = ((dataOut & (1 << i)) > 0);

            //Sets the pin to HIGH or LOW depending on pinState
            pigpio.gpioWrite(dataPin, pinState);
            //register shifts bits on upstroke of clock pin
            pigpio.gpioWrite(clockPin, true);
            //zero the data pin after shift to prevent bleed through
            pigpio.gpioWrite(dataPin, false);
        }

        //stop shifting
        pigpio.gpioWrite(clockPin, false);
    }

    public void run() {

        String host = "pigpiod-host";

        int latchPin = 23;  // gpio connected to ST_CP pin of 74HC595
        int clockPin = 24;  // gpio connected to SH_CP pin of 74HC595
        int dataPin = 25;   // gpio connected to DS pin of 74HC595

        byte data[] = new byte[] {1,2}; // data to be sent out

        try {
            System.out.println("Opening Pigpio.");

            pigpio = new PigpioSocket(host, 8888);   // connect to pigpiod via socket interface
            // pigpio = new Pigpio();                // connect to pigpiod directly/locally

            pigpio.gpioInitialize();
            Utils.addShutdown(pigpio);

            // display data stored in array fields
            pigpio.gpioWrite(latchPin, false);     // set latch => here comes the data
            shiftOut(dataPin, clockPin, data[1]);  // send next 8 bits, etc.
            shiftOut(dataPin, clockPin, data[0]);  // send 8 bits
            pigpio.gpioWrite(latchPin, true);      // release latch => we are done

            pigpio.gpioTerminate();

        } catch (PigpioException e) {
            e.printStackTrace();
        }

        System.out.println("Test LEDs Completed.");
    }


}
