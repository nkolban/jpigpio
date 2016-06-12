package jpigpio.packet;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Pulse;
import jpigpio.Utils;

import java.util.ArrayList;

/**
 * Class implementing 433 MHz transmitter (e.g. FS1000A) using pigpiod daemon.
 * Specific protocol (high/low signal duration, datagram/message length etc. can be cofigured
 * by passing different Protocol object to the constructor of this class.
 * <br><br>
 * Work is based on Robert Tidey LightwaveRF code https://github.com/roberttidey/LightwaveRF
 *
 * Example usage: see Test_Rf433Tx
 */
public class Rf433tx {
    JPigpio pi;
    int txGpio;  // GPIO pin transmitter is connected to
    int txBit;

    Protocol protocol;
    Transmitter transmitter;

    /**
     * Inner class responsible for converting pulses to pigpiod waves and transmitting them in a separate thread.
     */
    class Transmitter implements Runnable {

        Thread thread;
        boolean stop = false;
        boolean error = false;
        boolean busy = false;
        Exception lastEx;
        JPigpio pi;

        ArrayList<ArrayList<Pulse>> wavePulses = new ArrayList<>();

        Transmitter(JPigpio jPigpio){
            this.pi = jPigpio;
        }

        @Override
        public void run(){
            int waveId=-1;
            while (!stop){
                try {
                    if (wavePulses.size() > 0 && !pi.waveTxBusy()) {

                        busy = true;

                        //pi.waveTxStop();
                        pi.waveAddNew();

                        // Set TX high and wait to get agc of RX trained
                        pi.gpioWrite(txGpio,true);
                        Thread.sleep(protocol.TX_PULSE_MSGGAP / 1000);

                        // create wave & "upload" it to pigpiod for transmitting
                        pi.waveAddGeneric(wavePulses.remove(0));
                        waveId = pi.waveCreate();

                        // repeat packet sending according to protocol parameters
                        for (int r = 0; r < protocol.DGRM_REPEAT_TX && !stop; r++) {
                            pi.waveSendOnce(waveId);
                            Thread.sleep(50);
                            while (pi.waveTxBusy()) {
                                Thread.sleep(2);  // empiric value of 2ms
                            }
                        }

                        // removeListener waveform after it has been transmitted
                        pi.waveDelete(waveId);

                        busy = false;

                    } else // nothing to send or busy => sleep
                        Thread.sleep(10);
                } catch (InterruptedException e) {
                    //TODO: not expecting this to happen
                } catch (PigpioException e) {
                    error = true;
                    lastEx = e;
                    System.out.println("Error: while sending wave "+waveId);
                }

            }

        }

        /**
         * Add pulse to transmitter queue
         * @param pulse Pulse to be transmitted
         */
        void addWave( ArrayList<Pulse> pulse){
            this.wavePulses.add(pulse);
        }

        /**
         * Read status of transmitter.
         * @return true if not transmitting and there are no pulses waiting to be transmitted
         * @throws PigpioException
         */
        boolean ready() throws PigpioException {
            return this.wavePulses.size() == 0 && !busy;
        }

        void start(){
            if (thread == null)
            {
                thread = new Thread (this);
                thread.setName("WaveTransmitter");
                thread.start ();
            }
        }

        /**
         * Stop transmitter
         * @throws PigpioException
         */
        void stop() throws PigpioException{
            this.stop = true;
            pi.waveTxStop();
        }

    }

    public Rf433tx(JPigpio pi, int txGpio, Protocol protocol) throws PigpioException {
        this.pi = pi;
        this.txGpio = txGpio;
        this.txBit = (1<< txGpio);

        this.protocol = protocol;
        this.transmitter = new Transmitter(pi);
        this.transmitter.start();

        pi.waveClear();
        pi.gpioSetMode(txGpio, JPigpio.PI_OUTPUT);
    }

    /**
     * Construct array of pulses from datagram data
     * @param data Data (4bit nibbles) to be transmitted
     * @return ArrayList of Pulses
     */
    public ArrayList<Pulse> constructMessagePulses(byte[] data){
        ArrayList<Pulse> wf;
        int dataByte;

        // Define a single datagram waveform
        wf = new ArrayList<>();
        // Pre Message low gap
        wf.add(new Pulse(0, txBit, protocol.TX_PULSE_MSGGAP));
        // Message start pulse
        wf.add(new Pulse(txBit, 0, protocol.TX_PULSE_HIGH));
        wf.add(new Pulse(0, txBit, protocol.TX_PULSE_HIGH));

        for(byte i: data){
            wf.add(new Pulse(txBit,0,protocol.TX_PULSE_HIGH));
            wf.add(new Pulse(0, txBit, protocol.TX_PULSE_HIGH));
            dataByte = protocol.nibble2sym(i);
            for (byte j = 0; j<8; j++)
                if ((dataByte & (0x80>>j)) != 0){
                    wf.add(new Pulse(txBit,0,protocol.TX_PULSE_HIGH));
                    wf.add(new Pulse(0, txBit, protocol.TX_PULSE_HIGH));
                } else
                    wf.add(new Pulse(0, 0, protocol.TX_PULSE_LOW));

        }

        // Message end pulse
        wf.add(new Pulse(txBit,0,protocol.TX_PULSE_HIGH));
        wf.add(new Pulse(0, txBit, protocol.TX_PULSE_HIGH));

        return wf;
    }


    /**
     * Converts provided data to waveforms using properties of Protocol
     * and transmits waveforms repeatedly (if required by Protocol).
     *
     * @param data data to be transmitted
     * @return
     * 0 - everything is OK
     * -1 - data
     * @throws PigpioException  on pigpiod error
     */
    public int put (byte[] data) throws PigpioException{
        if (data.length != protocol.DATA_SIZE)
            return -1;

        return putNibbles(Utils.bytes2nibbles(data));
    }

    /**
     * Converts provided nibbles (4bit stored in 8bit) to waveforms using properties of Protocol
     * and transmits waveforms repeatedly (if required by Protocol).
     *
     * @param nibbles
     * nibbles stored in bytes to transmit.
     * @return
     * 0 - everything is OK
     * -1 - data
     * @throws PigpioException
     */
    int putNibbles (byte[] nibbles) throws PigpioException{
        int ret = 0;
        ArrayList<Pulse> wf;

        if (nibbles.length < protocol.DGRM_LENGTH)
            return -1;

        wf = constructMessagePulses(nibbles);
        transmitter.addWave(wf);

        return ret;

    }


    /**
     * Returns TRUE if available for another transmission.
     * @return
     * TRUE/FALSE
     * @throws PigpioException  on pigpiod error
     */
    public synchronized boolean ready() throws PigpioException {
        return transmitter.ready();
    }


    /**
     * Terminates transmission of all waveforms.
     * @throws PigpioException  on pigpiod error
     */
    public void terminate() throws PigpioException{
        transmitter.stop();
        pi.waveClear();
    }


}
