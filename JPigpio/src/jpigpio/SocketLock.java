package jpigpio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by Jozef on 19.04.2016.
 */
public class SocketLock {

    String host;
    int port;
    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    int replyTimeout = 10000;

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

    public synchronized int sendCmd(int cmd, int p1, int p2) throws IOException {
        byte[] b = {};

        return sendCmd(cmd, p1, p2, 0, b);
    }

    public synchronized int sendCmd(int cmd, int p1, int p2, int p3, byte[] ext) throws IOException {
        int resp, a, w;
        out.writeInt(Integer.reverseBytes(cmd));
        out.writeInt(Integer.reverseBytes(p1));
        out.writeInt(Integer.reverseBytes(p2));
        out.writeInt(Integer.reverseBytes(p3));
        if (ext.length > 0)
            out.write(ext);
        out.flush();

        w = replyTimeout;
        a = in.available();

        //System.out.println("#1 "+cmd);
        while (w > 0 && a < 16){
            w -= 100;
            try{ wait(100); } catch (InterruptedException e) {}
            a = in.available();
            //System.out.println("#2 "+a+"w="+w);
        }

        if (in.available() < 16)
            throw new IOException("Timeout: No response from RPi withing "+ replyTimeout +" ms.");

        resp = Integer.reverseBytes(in.readInt()); // ignore response
        resp = Integer.reverseBytes(in.readInt()); // ignore response
        resp = Integer.reverseBytes(in.readInt()); // ignore response
        resp = Integer.reverseBytes(in.readInt()); // contains error or response
        return resp;
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
