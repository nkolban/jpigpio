package jpigpio.packet;

/**
 * Class defining properties of RF communication signaling.
 */
public class Protocol {

    /**
     * datagram data size (in bytes)
     */
    public int DATA_SIZE = 5;

    //############# DATAGRAMS
    /**
     * datagram length in bytes (always double of DATA_SIZE because datagrams are constructed out of nibbles (4bit))
     */
    public int DGRM_LENGTH = DATA_SIZE*2;

    public int DGRM_REPEAT = 3;
    /**
     * repeat transmission of each datagram this many times to make sure at least one got through
     */
    public int DGRM_REPEAT_TX = DGRM_REPEAT;

    /**
     * report this many same datagrams as a single datagram (eliminate duplicates)
     */
    public int DGRM_REPEAT_RX = DGRM_REPEAT;

    public final int DGRM_RX_TIMEOUT = 1000000;    // datagram timeout (microseconds)

    public boolean DGRM_KEEP_ON_ENCODING_ERROR = true;    // receive datagram even if symbol error occured
    // => some data will be corrupted


    //############# SIGNALING
    /**
     * gap between two datagrams (microsec)
     */
    public final int TX_PULSE_MSGGAP = 10800;

    /**
     * duration of high pulse (microsec)
     */
    public final int TX_PULSE_HIGH = 280;

    /**
     * duration of low pulse (microsec)
     */
    public final int TX_PULSE_LOW = 980;

    /**
     * pulse too short to be considered meaningful (microsec)
     */
    public final int RX_PULSE_TOOSHORT = 150;

    /**
     * short pulse = 1 (one) (microsec)
     */
    public final int RX_PULSE_ONE = 500;

    /**
     * long pulse = 0 (zero) (microsec)
     */
    public final int RX_PULSE_ZERO = 2000;

    /**
     * gap between datagrams (microsec)
     */
    public final int RX_PULSE_MSGGAP = 5000;

    //############# ENCODING
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

    public void setDataSize(byte size){
        DATA_SIZE = size;

    }
}
