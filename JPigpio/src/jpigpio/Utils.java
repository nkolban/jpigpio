package jpigpio;

public class Utils {
	/**
	 * Add a handler to perform a clean termination of pigpio on termination.
	 * @param pigpio pigpio object to terminate
	 */
	public static void addShutdown(JPigpio pigpio) {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				pigpio.gpioTerminate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
	} // End of assShutdown
	
	public static int mapToInt(int value, int sourceLow, int sourceHigh, int targetLow, int targetHigh) {
		double pos = (value-sourceLow)/(double)(sourceHigh - sourceLow);
		//System.out.println("Pos: " + pos);
		//System.out.println("tHigh - tLow = " + (targetHigh - targetLow));
		//System.out.println("r = " + (targetLow + ((targetHigh - targetLow) *  pos)));
		return (int)(targetLow + ((targetHigh - targetLow) *  pos));
	}
	
	/**
	 * Return a binary string representation of the byte.
	 * @param value byte to convert
	 * @return A binary string representation of the byte.
	 */
	public static String byteToBinary(byte value) {
		return String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');
	} // End of byteToBinary
	
	/**
	 * Return a binary string representation of the first 16 bits of the integer.
	 * @param value integer to convert
	 * @return A binary string representation of the first 16 bits of the integer.
	 */
	public static String int16ToBinary(int value) {
		//System.out.println(String.format("Value: 0x%x", value));
		if (Integer.toUnsignedLong(value) != Integer.toUnsignedLong(value & 0xffff)) {
			System.out.println(String.format("Warning: value is: 0x%x while masked value is 0x%x", Integer.toUnsignedLong(value), value & 0xffff));
		}
		return splitBinary4(String.format("%16s", Integer.toBinaryString(value & 0xffff)).replace(' ', '0'));
	} // End of int16ToBinary
	
	/**
	 * Given an array of 2 bytes, return an int representation using the 1st byte as
	 * the MSByte.
	 * @param word The 2 bytes of data.
	 * @return An int representation of the two bytes of data.
	 */
	public static int byteWordToInt(byte word[]) {
		return (int)((Byte.toUnsignedLong(word[0]) << 8) | Byte.toUnsignedLong(word[1]));
	} // End of byteWordToInt
	
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
	
	/**
	 * Set the bit within the int.
	 * @param value The value in which to set the bit.
	 * @param bit The bit to set.
	 * @return The new value with the bit set.
	 */
	public static int setBit(int value, int bit) {
		assert bit >=0 && bit<16;
		return value | bitMask(bit);
	} // End of setBit
	
	/**
	 * Set the bit within the byte.
	 * @param value The value in which to set the bit.
	 * @param bit The bit to set.
	 * @return The new value with the bit set.
	 */
	public static byte setBit(byte value, int bit) {
		assert bit >=0 && bit<16;
		return (byte)(value | bitMask(bit));
	} // End of setBit
	
	/**
	 * Clear the bit within the int.
	 * @param value The value in which the bit is to be cleared.
	 * @param bit The bit to clear.
	 * @return The value with the bit cleared.
	 */
	public static int clearBit(int value, int bit) {
		assert bit >=0 && bit<16;
		return value & ~bitMask(bit);
	} // End of clear bit
	
	/**
	 * Clear the bit within the byte.
	 * @param value The value in which the bit is to be cleared.
	 * @param bit The bit to clear.
	 * @return The value with the bit cleared.
	 */
	public static byte clearBit(byte value, int bit) {
		assert bit >=0 && bit<16;
		return (byte)(value & ~bitMask(bit));
	} // End of clear bit
	
	/**
	 * Calculate a bitmask of the bit.
	 * @param bit The bit to be used to calculate the bitmask.
	 * @return The value of the bitmask.
	 */
	public static int bitMask(int bit) {
		assert bit >=0 && bit<16;
		return 1<<bit;
	} // End of bitMask
	
	/**
	 * Return true if the corresponding bit of the data is set.
	 * @param value The data to test.
	 * @param bit The bit to examine.
	 * @return True if the corresponding bit in the data is set.
	 */
	public static boolean isSet(int value, int bit) {
		assert bit >=0 && bit<16;
		return (value & bitMask(bit)) != 0;
	} // End of isSet
	
	/**
	 * Return true if the corresponding bit of the data is set.
	 * @param value The data to test.
	 * @param bit The bit to examine.
	 * @return True if the corresponding bit in the data is set.
	 */
	public static boolean isSet(byte value, int bit) {
		assert bit >=0 && bit<16;
		return (value & bitMask(bit)) != 0;
	} // End of isSet
	
	/**
	 * Dump an array of bytes
	 * @param data The data to dump
	 * @return A string representation of the data.
	 */
	public static String dumpData(byte data[]) {
		String ret = "";
		for (int i=0; i<data.length; i++) {
			ret += String.format("%2x", data[i]).replace(' ', '0') + " ";
		}
		return ret;
	} // End of dumpData
	
	/**
	 * Return a string representation of "0" or "1" as the value of a boolean.
	 * @param value A boolean to examine.
	 * @return The string "1" is the boolean is true and "0" if the boolean is false.
	 */
	public static String bitString(boolean value) {
		return value?"1":"0";
	} // End of bitString

	/**
	 *  Returns the microsecond difference between two ticks.
	 *  This function handles rollover of ticks as ticks are only 32bit.
	 *
	 * ...
	 * print(pigpio.tickDiff(4294967272, 12))
	 * 36
	 * ...
	 * @param olderTick
	 * 	tick 1
	 * @param tick
	 * 	tick 2
     * @return
	 * 	difference between ticks
     */
	public static long tickDiff(long olderTick, long tick) {
		int tDiff = (int)(tick - olderTick);
		return Integer.toUnsignedLong(tDiff);
	}

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] bytes2nibbles(byte[] bytes){
		byte[] nibb = new byte[bytes.length*2];

		for (int i = 0; i<bytes.length;i++){
			nibb[i*2] = (byte)(bytes[i] >> 4);
			nibb[i*2+1] = (byte)(bytes[i] & 0x0F);
		}
		return nibb;
	}

	public static byte[] nibbles2bytes(byte[] nibb){
		byte[] bytes = new byte[nibb.length/2];

		for (int i = 0; i<bytes.length;i++)
			bytes[i] = (byte)(nibb[i*2]<<4 | nibb[i*2+1]);

		return bytes;
	}

	public static Long LEint2Long(byte[] bytes){
		byte[] b = new byte[8];
		Long ret;
		for (int i=0;i<4;i++)
			b[7-i] = bytes[i];

		// then convert bytes to long (as Java has no unsigned int, let's use long)
		ret = java.nio.ByteBuffer.wrap(b).getLong();

		return ret;
	}

} // End of class
// End of file