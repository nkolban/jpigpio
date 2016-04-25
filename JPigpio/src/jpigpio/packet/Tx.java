package jpigpio.packet;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Pulse;

import java.util.ArrayList;

/**
 * Created by Jozef on 24.04.2016.
 */
public class Tx {
    JPigpio pi;
    int txGpio;
    int txBit;

    Protocol protocol;
    Transmitter transmitter;

    class Transmitter implements Runnable {

        Thread thread;
        boolean stop = false;
        boolean error = false;
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
                    //System.out.println("TX Queue="+waveIds.size());
                    if (wavePulses.size() > 0 && !pi.waveTxBusy()) {

                        pi.waveTxStop();
                        pi.waveDelete(waveId); // remove waveform after it has been transmitted
                        pi.waveAddNew();

                        pi.waveAddGeneric(wavePulses.remove(0));
                        waveId = pi.waveCreate();

                        //System.out.println("Sending waveid="+waveId);

                        // Set TX high and wait to get agc of RX trained
                        pi.gpioWrite(txGpio,true);
                        Thread.sleep(protocol.TX_PULSE_MSGGAP / 1000);

                        // repeat packet sending according to protocol parameters
                        for (int r = 0; r < protocol.TX_REPEAT; r++) {
                            pi.waveSendOnce(waveId);
                            while (pi.waveTxBusy())
                                Thread.sleep(1);
                        }


                    } else // nothing to send or busy => sleep
                        Thread.sleep(10);
                } catch (InterruptedException e) {
                    //TODO: not expecting this to happen
                } catch (PigpioException e) {
                    error = true;
                    lastEx = e;
                    //System.out.println("Error: while sending wave "+waveId);
                }

            }

        }

        void addWave( ArrayList<Pulse> pulse){
            wavePulses.add(pulse);
        }

        boolean ready() throws PigpioException {
            return wavePulses.size() == 0 && !pi.waveTxBusy();
        }

        void start(){
            if (thread == null)
            {
                thread = new Thread (this);
                thread.setName("WaveTransmitter");
                thread.start ();
            }
        }

        void stop() throws PigpioException{
            this.stop = true;
            pi.waveTxStop();
        }

    }

    public Tx(JPigpio pi, int txGpio, Protocol protocol) throws PigpioException {
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
     * Converts provided data to waveforms using properties of Protocol
     * and transmits waveforms repeatedly (if required by Protocol).
     *
     * @param data
     * Data packet to transmit.
     * IMPORTANT data is an array of nibbles (4bit).
     * @return
     * 0 - everything is OK
     * -1 - data
     * @throws PigpioException
     */
    public int put (byte[] data) throws PigpioException{
        int ret = 0;
        ArrayList<Pulse> wf;
        int dataByte;

        if (data.length < protocol.MSG_LENGTH)
            return -1;

        // Define a single message waveform
        wf = new ArrayList<>();
        // Pre Message low gap
        wf.add(new Pulse(0, txBit, protocol.TX_PULSE_MSGGAP));
        // Message start pulse
        wf.add(new Pulse(txBit, 0, protocol.TX_PULSE_HIGH));
        wf.add(new Pulse(0, txBit, protocol.TX_PULSE_HIGH));

        for(byte i: data){
            wf.add(new Pulse(txBit,0,protocol.TX_PULSE_HIGH));
            wf.add(new Pulse(0, txBit, protocol.TX_PULSE_HIGH));
            dataByte = protocol.SYMBOL[i & 0x0F];
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

        transmitter.addWave(wf);

        return ret;

    }


    /**
     * Returns TRUE if available for another transmission.
     * @return
     * TRUE/FALSE
     * @throws PigpioException
     */
    public synchronized boolean ready() throws PigpioException {
        return transmitter.ready();
    }


    /**
     * Terminates transmission of all waveforms.
     * @throws PigpioException
     */
    public void terminate() throws PigpioException{
        transmitter.stop();
        pi.waveClear();
    }


}
