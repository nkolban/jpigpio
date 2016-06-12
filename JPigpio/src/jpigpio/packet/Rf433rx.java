package jpigpio.packet;

import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

import java.util.ArrayList;

/**
 * Class implementing RF 433 MHz communication receiver (e.g. XD-RF-5V).
 * It implements NotificationReceiver which is decoding pulses based on preset Protocol
 * (you can tweak signalling by creating your own Protocol). NotificationListener is plugged
 * to PigpioSocket NotificationRouter, receiving every notification received from pigpiod daemon.
 * Received datagrams can be accessed via method get()
 * <br><br>
 * Work is based on Robert Tidey LightwaveRF code https://github.com/roberttidey/LightwaveRF
 */
public class Rf433rx {

    JPigpio pi;
    int rxGpio;

    NotificationListener cb;

    // Protocol describing signal levels, datagram length etc.
    Protocol protocol;

    /**
     * ArrayList of datagrams received from transmitter.
     */
    ArrayList<byte[]> datagrams = new ArrayList<>();

    /**
     * Class handling all notifications coming from pigpiod.
     * Technically this means this class analyzes signals received by pigpiod
     * and creates datagrams out of them.
     */
    class RxNotificationListener extends NotificationListener {

        final int RX_STATE_IDLE = 0;
        final int RX_STATE_MSGSTARTFOUND = 1;
        final int RX_STATE_BYTESTARTFOUND = 2;
        final int RX_STATE_GETBYTE = 3;

        boolean datagramError = false;

        int messageTick = 0;
        long lastTick = 0;
        long pulse = 0;
        int state = RX_STATE_IDLE;
        //int data1 = 0;

        int repeatCount = 0;
        boolean duplicate = false;

        int dataBit = 0;
        int dataByte = 0;

        byte[] datagram = new byte[protocol.DGRM_LENGTH];

        int data = 0;

        String pulses = "";

        RxNotificationListener(int userGpio, int edge){
            super(userGpio, edge);
            try {
                lastTick = pi.getCurrentTick();
            } catch (PigpioException e) {
                // TODO: handle somehow
            }
        }

        @Override
        public void alert(int gpio, int level, long tick){
            int trans = 0;

            count++;  // increase called count for statistical purposes

            if (level == pi.PI_TIMEOUT) { // TIMEOUT notification received from PIGPIO?
                try {
                    pi.setWatchdog(rxGpio, 0); // Switch watchdog off.
                } catch (PigpioException e) {
                    // TODO: handle somehow
                }
                return;
            }

            pulse = Utils.tickDiff(lastTick, tick);
            lastTick = (int)tick;

            if (pulse < protocol.RX_PULSE_TOOSHORT) // very short pulse - ignore it
                return;
            else if (state == RX_STATE_IDLE && pulse <= protocol.RX_PULSE_MSGGAP) // quick check to see worth proceeding
                return;
            else if (pulse < protocol.RX_PULSE_ONE) {   // normal short pulse
                trans = level + 2;
                pulses += "1";
            } else if (pulse < protocol.RX_PULSE_ZERO) {  // normal long pulse
                trans = level + 4;
                pulses += "0";
            } else if (pulse > protocol.RX_PULSE_MSGGAP) { // gap between datagrams
                trans = level + 6;
                pulses += " ";
            } else
                trans = 8;

            // state machine
            switch (state) {
                //-----------------------------
                case RX_STATE_IDLE:
                    if (trans == 7) { // 1 after datagram gap
                        state = RX_STATE_MSGSTARTFOUND;
                        duplicate = true;
                    }
                    break;
                //-----------------------------
                case RX_STATE_MSGSTARTFOUND:
                    if (trans == 2)         // nothing to do, wait for next 1
                        trans = trans;
                    else if (trans == 3) {  // start of byte detected
                        dataByte = 0;
                        state = RX_STATE_BYTESTARTFOUND;
                    } else
                        state = RX_STATE_IDLE;
                    break;
                //-----------------------------
                case RX_STATE_BYTESTARTFOUND:
                    if (trans == 2)
                        trans = trans;
                    else if (trans == 3) {  // 1 160->500
                        data = 0;
                        dataBit = 0;
                        state = RX_STATE_GETBYTE;
                    } else if (trans == 5) {  // 0 500->1500
                        data = 0;
                        dataBit = 1;
                        state = RX_STATE_GETBYTE;
                    } else
                        state = RX_STATE_IDLE;
                    break;
                //-----------------------------
                case RX_STATE_GETBYTE:
                    if (trans == 2)
                        trans = trans;
                    else if (trans == 3) {      // 1 160->500
                        data = data << 1 | 1;
                        dataBit += 1;
                    } else if (trans == 5) {     // 0 500->1500
                        data = data << 2 | 2;
                        dataBit += 2;
                    } else
                        state = RX_STATE_IDLE;

                    // check if byte complete
                    if (dataBit >= 8) {

                        data = protocol.sym2nibble(data);

                        // negative means: not found in nibbles => byte error
                        if (data < 0) {
                            datagramError = true;
                            byteErrorCount++;
                        } else {
                            // first received byte different from the same byte from previous datagram
                            // means this datagram is not a duplicate of the previous one
                            if (data != datagram[dataByte]) {
                                duplicate = false;
                                repeatCount = 0;
                            }

                            datagram[dataByte] = (byte) data;
                        }

                        dataByte++;
                        dataBit = 0;
                        pulses = "";

                        if (dataByte >= protocol.DGRM_LENGTH) {  //datagram complete?
                            //System.out.println("#1 datagram received: "+datagram.toString());

                            if (Utils.tickDiff(messageTick, (int) lastTick) > protocol.DGRM_RX_TIMEOUT || messageTick == 0) {
                                repeatCount = 0;
                                duplicate = false;
                            } else if (duplicate)
                                repeatCount++;

                            if (repeatCount >= protocol.DGRM_REPEAT_RX){
                                repeatCount = 0;
                                duplicate = false;
                            }

                            // if no datagram error (or ignoring datagram errors) and not duplicate
                            // System.out.println("#2 conditions: "+ protocol.DGRM_KEEP_ON_ENCODING_ERROR + " : " + datagramError + " # " + duplicate);
                            if ((protocol.DGRM_KEEP_ON_ENCODING_ERROR || !datagramError) && !duplicate) {
                                // System.out.println("#3 datagram processed: "+datagram.toString());
                                datagrams.add(Utils.nibbles2bytes(datagram));
                            }

                            state = RX_STATE_IDLE;
                            //messageTick = messageTick;
                            messageTick = (int)tick;

                            if (datagramError)
                                datagramErrorCount++;
                            datagramError = false;

                        } else
                            state = RX_STATE_BYTESTARTFOUND;
                    }
                    break;
            }

        }

    }


    public Rf433rx(JPigpio pi, int rxGpio, Protocol protocol) throws PigpioException{
        this.pi = pi;
        this.rxGpio = rxGpio;
        this.protocol = protocol;

        pi.gpioSetMode(rxGpio, JPigpio.PI_INPUT);

        setCallback(new RxNotificationListener(rxGpio, JPigpio.PI_EITHER_EDGE));

    }

    public void setCallback(NotificationListener notificationListener) throws PigpioException{
        this.cb = notificationListener;
        pi.addCallback(cb);
    }

    /**
     * Get one packet from available packets.
     * Check if there are packets available before calling this method.
     * @return Datagram.
     * IMPORTANT: Datagram contains nibbles 4bit) stored in bytes.
     * @throws IndexOutOfBoundsException if there is no datagram available
     */
    public byte[] get() throws IndexOutOfBoundsException {
        return datagrams.remove(0);
    }

    /**
     * Returns number of packets available for pickup
     * @return
     * Number of datagrams available.
     */
    public int available(){
        return datagrams.size();
    }

    /**
     * Terminate receiving thread, cancel further notifications from pigpiod.
     * @throws PigpioException  on pigpiod error
     */
    public void terminate() throws PigpioException{
        if (cb != null) {
            pi.removeCallback(cb);
            pi.setWatchdog(rxGpio, 0);
            cb = null;
        }

    }

    /**
     * Simple statistics of byte errors detected while receiving datagrams.
     * High number of errors might mean low signal strength or too long datagrams.
     * @return byte error count
     */
    public int byteErrorCount(){
        return cb.byteErrorCount();
    }

    /**
     * Simple statistics returning number of errors in datagrams.
     * @return number of errors in datagrams.
     */
    public int datagramErrorCount(){
        return cb.datagramErrorCount();
    }


}
