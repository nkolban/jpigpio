package jpigpio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * A helper class for some simple file I/O activities.
 *
 */
public class FileIO {
	private File file;
	private FileInputStream fis;
	
	/**
	 * Constructor to create a wrapper to the file.
	 * @param file The file to be accessed.
	 */
	public FileIO(File file) {
		this.file = file;
	} // End of constructor
	
	/**
	 * Read up to <pre>size</pre> bytes from the file.
	 * @param size The maximum size of data to return
	 * @return An array of data.  It may be zero length if there is no data available.
	 * @throws IOException on IO Exception :-)
	 */
	public byte[] read(int size) throws IOException {
		if (fis == null) {
			fis = new FileInputStream(file);
		}
		// Read either the maximum available data or size requested bytes (which ever is smaller)
		byte data[] = new byte[Math.min(fis.available(), size)];
		
		// Don't try and read 0 bytes of data ...
		if (data.length > 0) {
			fis.read(data);
		}
		return data;
	} // End of close
	
	/**
	 * Close the file.
	 * @throws IOException on IO Exception :-)
	 */
	public void close() throws IOException {
		if (fis != null) {
			fis.close();
		}
		fis = null;
	} // End of close
} // End of class
// End of file