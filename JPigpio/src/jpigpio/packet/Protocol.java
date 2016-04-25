package jpigpio.packet;

/**
 * Created by Jozef on 24.04.2016.
 */
public class Protocol {
    public int MSG_LENGTH = 10; // message length in bytes

    public int TX_PULSE_MSGGAP = 10800;     // gap between two messages
    public int TX_PULSE_HIGH = 280;         // duration of high pulse
    public int TX_PULSE_LOW = 980;          // duration of low pulse
    public int TX_REPEAT = 5;               // repeat transmission of each packet this many times


    public int RX_PULSE_TOOSHORT = 150;     // pulse too short to be considered meaningful
    public int RX_PULSE_ONE = 500;          // short pulse = 1 (one)
    public int RX_PULSE_ZERO = 2000;        // long pulse = 0 (zero)
    public int RX_PULSE_MSGGAP = 5000;      // gap between messages
    public int RX_MSG_TIMEOUT = 1000000;    // message timeout
    public int RX_REPEAT = 0;               // report this many same packets as a single packet


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
        //System.out.println(String.format("Symbol %X, data1 %X",symbol,s));
        //Arrays.asList(SYMBOL).indexOf(symbol);
        return s;
    }
}
