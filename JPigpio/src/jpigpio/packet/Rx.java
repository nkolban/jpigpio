package jpigpio.packet;

import jpigpio.Callback;
import jpigpio.JPigpio;
import jpigpio.PigpioException;
import jpigpio.Utils;

import java.util.ArrayList;

/**
 * Created by Jozef on 24.04.2016.
 */
public class Rx {

    JPigpio pi;
    int rxGpio;


    Callback cb;

    // Protocol describing signal levels, message length etc.
    Protocol protocol;

    ArrayList<byte[]> messages = new ArrayList<>();

    public int calledCount = 0;

    /**
     * Class handling all notifications coming from pigpiod.
     * Technically this means this class analyzes signals received by pigpiod
     * and creates packets out of them.
     */
    class RxCallback extends Callback {

        public final int RX_STATE_IDLE = 0;
        public final int RX_STATE_MSGSTARTFOUND = 1;
        public final int RX_STATE_BYTESTARTFOUND = 2;
        public final int RX_STATE_GETBYTE = 3;

        public int byteErrorCount = 0;     //byte error count
        public int packetErrorCount = 0;

        long lastTick = 0;
        long pulse = 0;
        int state = RX_STATE_IDLE;
        int data1 = 0;

        int repeatCount = 0;
        boolean duplicate = false;
        int messageTick = 0;

        int dataBit = 0;
        int dataByte = 0;

        boolean packetError = false;

        byte[] message = new byte[protocol.MSG_LENGTH];

        int data = 0;

        String pulses = "";



        RxCallback(int userGpio, int edge){
            super(userGpio, edge);
            try {
                lastTick = pi.getCurrentTick();
            } catch (PigpioException e) {
                // TODO: handle somehow
            }
        }

        @Override
        public void func(int gpio, int level, long tick){
            int trans = 0;
            calledCount ++;

            if (level == pi.PI_TIMEOUT) { // TIMEOUT notification received from PIGPIO?
                try {
                    pi.setWatchdog(rxGpio, 0); // Switch watchdog off.
                } catch (PigpioException e) {
                    // TODO: handle somehow
                }
                return;
            }

            pulse = tick - lastTick;
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
            } else if (pulse > protocol.RX_PULSE_MSGGAP) { // gap between messages
                trans = level + 6;
                pulses += " ";
            } else
                trans = 8;

            // state machine
            switch (state) {
                //-----------------------------
                case RX_STATE_IDLE:
                    if (trans == 7) { // 1 after message gap
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
                        data1 = data;
                        data = protocol.sym2nibble(data);
                        System.out.println(String.format("#data: "+pulses+" = %d = data1: %X",data, data1));

                        // negative means: not found in nibbles => byte error
                        if (data < 0) {
                            //packetError = true;
                            byteErrorCount++;
                        } else {
                            // first received byte different from the same byte from previous packet
                            // means this packet is not a duplicate of the previous one
                            if (data != message[dataByte]) {
                                duplicate = false;
                                repeatCount = 0;
                            }

                            message[dataByte] = (byte) data;
                        }

                        dataByte++;
                        dataBit = 0;
                        pulses = "";

                        if (dataByte >= protocol.MSG_LENGTH) {  //packet complete?

                            //System.out.println("#22 "+duplicate+ " ++ "+packetError+" ++ "+protocol.RX_REPEAT+" ++ "+repeatCount);

                            if (Utils.tickDiff(messageTick, (int)lastTick) > protocol.RX_MSG_TIMEOUT || messageTick == 0)
                                repeatCount = 0;
                            else if (duplicate)
                                repeatCount++;

                            if (repeatCount >= protocol.RX_REPEAT){
                                repeatCount = 0;
                                duplicate = false;
                            }

                            if (!packetError && !duplicate)
                                messages.add(message.clone());

                            state = RX_STATE_IDLE;
                            //messageTick = messageTick;
                            messageTick = (int)tick;

                            if (packetError)
                                packetErrorCount++;
                            packetError = false;

                            //System.out.println("#99: "+Arrays.toString(message));
                            //System.out.println("#99: BER="+byteErrorCount +" PER="+packetErrorCount);
                            //System.out.println("#99: msgs="+messages.size());

                        } else
                            state = RX_STATE_BYTESTARTFOUND;
                    }
                    break;
            }

        }

    }


    public Rx (JPigpio pi, int rxGpio, Protocol protocol) throws PigpioException{
        this.pi = pi;
        this.rxGpio = rxGpio;
        this.protocol = protocol;

        pi.gpioSetMode(rxGpio, JPigpio.PI_INPUT);

        setCallback(new RxCallback(rxGpio, JPigpio.PI_EITHER_EDGE));

    }

    public void setCallback(Callback callback) throws PigpioException{
        this.cb = callback;
        pi.addCallback(cb);
    }

    /**
     * Get one packet from available packets.
     * Check if there are packets available before calling this method.
     * @return
     * Packet.
     * IMPORTANT: Packet contains nibbles 4bit) stored in bytes.
     * @throws IndexOutOfBoundsException
     */
    public byte[] get() throws IndexOutOfBoundsException {
        return messages.remove(0);
    }

    /**
     * Returns number of packets available for pickup
     * @return
     * Number of messages available.
     */
    public int available(){
        return messages.size();
    }

    /**
     * Terminate receiving thread, cancel further notifications from pigpiod.
     * @throws PigpioException
     */
    public void terminate() throws PigpioException{
        if (cb != null) {
            pi.removeCallback(cb);
            pi.setWatchdog(rxGpio, 0);
            cb = null;
        }

    }


}
