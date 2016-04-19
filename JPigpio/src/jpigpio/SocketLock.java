package jpigpio;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jozef on 19.04.2016.
 */
public class SocketLock {

    String host;
    int port;
    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    public SocketLock(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
        this.host = host;
        this.port = port;
    }

    public void terminate() throws IOException{

        in.close();
        in = null;

        out.flush();
        out.close();
        out = null;

        socket.close();
        socket = null;

    }

    /**
     * Helper function adding reversed ints to an output stream
     * @param stream
     * @param ints
     * @return
     */
    public static ByteArrayOutputStream streamInts(ByteArrayOutputStream stream, int... ints){
        for (int i:ints)
            stream.write(Integer.reverseBytes(i));

        return stream;

    }

    public synchronized int sendCmd(int cmd, int p1, int p2) throws IOException {
        int resp;
        out.writeInt(Integer.reverseBytes(cmd));
        out.writeInt(Integer.reverseBytes(p1));
        out.writeInt(Integer.reverseBytes(p2));
        out.writeInt(Integer.reverseBytes(0));
        out.flush();

        resp = in.readInt(); // ignore response
        resp = in.readInt(); // ignore response
        resp = in.readInt(); // ignore response
        resp = in.readInt(); // contains error or response
        return Integer.reverseBytes(resp);
    }

    public synchronized int sendCmd(int cmd, int p1, int p2, int p3, byte[] ext) throws IOException {
        int resp;
        out.writeInt(Integer.reverseBytes(cmd));
        out.writeInt(Integer.reverseBytes(p1));
        out.writeInt(Integer.reverseBytes(p2));
        out.writeInt(Integer.reverseBytes(p3));
        out.write(ext);
        out.flush();

        resp = in.readInt(); // ignore response
        resp = in.readInt(); // ignore response
        resp = in.readInt(); // ignore response
        resp = in.readInt(); // contains error or response
        return Integer.reverseBytes(resp);
    }


    /**
     * Write an integer to the pigpio demon
     *
     * @param i
     *            The value of the integer to write.
     * @throws IOException
     */
    public void writeInt(int i) throws IOException {
        out.writeInt(Integer.reverseBytes(i));
        out.flush();
        // byte data[] = new byte[4];
        // data[0] = (byte) (i & 0xff);
        // data[1] = (byte) ((i >> 8) & 0xff);
        // data[2] = (byte) ((i >> 16) & 0xff);
        // data[3] = (byte) ((i >> 24) & 0xff);
        // dataOutputStream.write(data);
    } // End of writeInt

    /**
     * Write a sequence of ints to the pigpio demon.
     *
     * @param args
     *            A set of ints to write to the pigpio demon.
     * @throws IOException
     */
    public void writeInt(int... args) throws IOException {
        for (int i : args) {
            writeInt(i);
        }
    } // End of writeInt

    /**
     * Write a sequence of bytes to the pigpio demon.
     *
     * @param data
     *            The sequence of bytes to write.
     * @throws IOException
     */
    public void writeBytes(byte data[]) throws IOException {
        out.write(data);
    } // End of writeBytes

    /**
     * Read an integer from the pigpio demon.
     *
     * @return
     * @throws IOException
     */
    public int readInt() throws IOException {
        // Read 4 bytes of data
        int r = in.readInt();
        // System.out.println("Read: " + Integer.reverseBytes(r));

        // Change the endian to Java
        return Integer.reverseBytes(r);
    } // End of readInt

    public void readBytes(byte[] data) throws IOException {
        in.readFully(data);
    } // End of readBytes

    @SuppressWarnings("unused")
    public int readPigpioResponse() throws IOException {
        int cmd = readInt();
        int p1 = readInt();
        int p2 = readInt();
        int resp = readInt();
        return resp;
    } // End of readPigpioResponse

}
