package jpigpio.packet;

/**
 * Created by Jozef on 24.04.2016.
 */
public class Protocol {

    //######### PACKET
    public final int PKT_DATA_LENGTH = 10;
    public final int PKT_CRC_BYTES = 0;

    //######### SIGNALING
    public final int TX_PULSE_MSGGAP = 10800;     // gap between two messages (microsec)
    public final int TX_PULSE_HIGH = 280;         // duration of high pulse (microsec)
    public final int TX_PULSE_LOW = 980;          // duration of low pulse (microsec)

    public final int RX_PULSE_TOOSHORT = 150;     // pulse too short to be considered meaningful (microsec)
    public final int RX_PULSE_ONE = 500;          // short pulse = 1 (one) (microsec)
    public final int RX_PULSE_ZERO = 2000;        // long pulse = 0 (zero) (microsec)
    public final int RX_PULSE_MSGGAP = 5000;      // gap between messages (microsec)

    //######### ENCODING

    public int[] SYMBOL = new int[]         // encoding of 4 bit nibbles into 8 bits (= byte) => error detection
            {0xF6,0xEE,0xED,0xEB,0xDE,0xDD,0xDB,0xBE,0xBD,0xBB,0xB7,0x7E,0x7D,0x7B,0x77,0x6F};

    public int sym2nibble(int symbol){
        int s = -1;
        int i = 0;
        while (i < 16 && s < 0)
            if (SYMBOL[i] == symbol)
                s = i;
            else
                i++;
        return s;
    }

    public int nibble2sym(int nibble){
        return SYMBOL[nibble & 0x0F];
    }

    //######### DATAGRAMS

    public final int DGRM_LENGTH = 10;           // datagram length in bytes

    public final int DGRM_REPEAT = 10;           // repeat datagram when sending/receiving
    public final int DGRM_REPEAT_TX = DGRM_REPEAT;    // repeat transmission of each datagram this many times
    public final int DGRM_REPEAT_RX = DGRM_REPEAT;    // report this many same datagrams as a single datagram

    public final int DGRM_RX_TIMEOUT = 10000000;    // frame receive timeout (microseconds)

    public final boolean DGRM_KEEP_ON_SYMBOL_ERROR = false; // keep datagram if symbol error occured




}
