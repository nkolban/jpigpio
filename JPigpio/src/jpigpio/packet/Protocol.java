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

    public int DGRM_REPEAT = 5;
    /**
     * repeat transmission of each datagram this many times to make sure at least one got through
     */
    public int DGRM_REPEAT_TX = DGRM_REPEAT;

    /**
     * report this many same datagrams as a single datagram (eliminate duplicates)
     */
    public int DGRM_REPEAT_RX = DGRM_REPEAT;

    public int DGRM_RX_TIMEOUT = 1000000;    // datagram timeout (microseconds)

    /**
     * Set this to False to discard datagrams where encoding error was detected.
     * Under normal operation it makes no sense to keep such datagrams.
     */
    public boolean DGRM_KEEP_ON_ENCODING_ERROR = false;


    //############# SIGNALING
    /**
     * gap between two datagrams (microsec)
     */
    public int TX_PULSE_MSGGAP = 10800;

    /**
     * duration of high pulse (microsec)
     */
    public int TX_PULSE_HIGH = 280;

    /**
     * duration of low pulse (microsec)
     */
    public int TX_PULSE_LOW = 980;

    /**
     * pulse too short to be considered meaningful (microsec)
     */
    public int RX_PULSE_TOOSHORT = 150;

    /**
     * short pulse = 1 (one) (microsec)
     */
    public int RX_PULSE_ONE = 500;

    /**
     * long pulse = 0 (zero) (microsec)
     */
    public int RX_PULSE_ZERO = 2000;

    /**
     * gap between datagrams (microsec)
     */
    public int RX_PULSE_MSGGAP = 5000;

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

    /**
     * Set maximum transmitted datagram size in bytes.
     * @param size size in bytes
     */
    public void setDataSize(int size){
        DATA_SIZE = size;
        DGRM_LENGTH = DATA_SIZE*2;
    }

    /**
     * Set transmission repeat count. By repeating transmission multiple times you are increasing probability to
     * receive data on the other end.
     * This also sets receiver repeat count - eliminating duplicate datagrams caused by repetitive transmissions.
     * @param repeatCount Count how many times datagram is going to be transmitted.
     */
    public void setRepeatCount(int repeatCount){
        DGRM_REPEAT = repeatCount;
        DGRM_REPEAT_TX = DGRM_REPEAT;
        DGRM_REPEAT_RX = DGRM_REPEAT;
    }

    public void setRxRepeatCount(int rxRepeatCount) {
        DGRM_REPEAT_RX = rxRepeatCount;
    }

    public void setTxRepeatCount(int txRepeatCount) {
        DGRM_REPEAT_TX = txRepeatCount;
    }
}
