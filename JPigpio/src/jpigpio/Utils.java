package jpigpio;

public class Utils {
	public static void addShutdown(JPigpio pigpio) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				pigpio.gpioTerminate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
	}
	
	public static int mapToInt(int value, int sourceLow, int sourceHigh, int targetLow, int targetHigh) {
		double pos = (value-sourceLow)/(double)(sourceHigh - sourceLow);
		System.out.println("Pos: " + pos);
		System.out.println("tHigh - tLow = " + (targetHigh - targetLow));
		System.out.println("r = " + (targetLow + ((targetHigh - targetLow) *  pos)));
		return (int)(targetLow + ((targetHigh - targetLow) *  pos));
	}
	
	/**
	 * Return a binary string representation of the byte.
	 * @param value
	 * @return A binary string representation of the byte.
	 */
	public static String byteToBinary(byte value) {
		return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
	} // End of byteToBinary
	
	/**
	 * Return a binary string representation of the first 16 bits of the integer.
	 * @param value
	 * @return A binary string representation of the first 16 bits of the integer.
	 */
	public static String int16ToBinary(int value) {
		//System.out.println(String.format("Value: 0x%x", value));
		if (Integer.toUnsignedLong(value) != Integer.toUnsignedLong(value & 0xffff)) {
			System.out.println(String.format("Warning: value is: 0x%x while masked value is 0x%x", Integer.toUnsignedLong(value), value & 0xffff));
		}
		return splitBinary4(String.format("%16s", Integer.toBinaryString(value & 0xffff)).replace(' ', '0'));
	} // End of int16ToBinary
	
	public static int byteWordToInt(byte word[]) {
		return (int)((Byte.toUnsignedLong(word[0]) << 8) | Byte.toUnsignedLong(word[1]));
	}
	
	/**
	 * Split a binary string into groups of 4 characters separated by spaces
	 * @param binaryString The original binary string
	 * @return An expanded binary string with spaces every 4 characters.
	 */
	private static String splitBinary4(String binaryString) {
		//System.out.println(binaryString + " " + binaryString.length());
		int i=0;
		String ret="";
		while(i< binaryString.length()) {
			if (i>0) {
				ret += " ";
			}
			if (i < binaryString.length()) {
				ret += binaryString.substring(i,i+4);
			}
			else {
				ret += binaryString.substring(i);
			}
			i+=4;
			//System.out.println(ret);
		}
		return ret;
	} // End of splitBinary4
	
	public static int setBit(int value, int bit) {
		return value | bitMask(bit);
	}
	
	public static int clearBit(int value, int bit) {
		return value & ~bitMask(bit);
	}
	
	public static int bitMask(int bit) {
		return 1<<bit;
	}
	
	public static boolean isSet(int value, int bit) {
		return (value & bitMask(bit)) != 0;
	}
	
	public static String dumpData(byte data[]) {
		String ret = "";
		for (int i=0; i<data.length; i++) {
			ret += String.format("%2x", data[i]).replace(' ', '0') + " ";
		}
		return ret;
	}
} // End of class
// End of file