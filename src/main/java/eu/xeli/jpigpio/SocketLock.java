package jpigpio;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;


/**
 * Created by Jozef on 19.04.2016.
 */
public class SocketLock {

    String host;            // pigpiod host
    int port;               // pigpiod port

    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    int replyTimeout = 10000; //milliseconds to wait for reply from pigpiod

    public SocketLock(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        reconnect();
    }

    public void reconnect() throws IOException{
        socket = new Socket(host, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
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

    /**
     * Send extended command to pigpiod and return result code
     * @param cmd Command to send
     * @param p1 Command parameter 1
     * @param p2 Command parameter 2
     * @param p3 Command parameter 3 (usually length of extended data - see paramater ext)
     * @param ext Array of bytes containing extended data
     * @return Command result code
     * @throws IOException in case of network connection error
     */
    public synchronized int sendCmd(int cmd, int p1, int p2, int p3, byte[] ext) throws IOException {
        int resp, a, w;

        ByteBuffer bb = ByteBuffer.allocate(16+ext.length);

        bb.putInt(Integer.reverseBytes(cmd));
        bb.putInt(Integer.reverseBytes(p1));
        bb.putInt(Integer.reverseBytes(p2));
        bb.putInt(Integer.reverseBytes(p3));

        if (ext.length > 0)
            bb.put(ext);

        out.write(bb.array());
        out.flush();

        w = replyTimeout;
        a = in.available();

        // if by any chance there is no response from pigpiod, then wait up to
        // specified timeout
        while (w > 0 && a < 16){
            w -= 10;
            try{ Thread.sleep(10); } catch (InterruptedException e) {}
            a = in.available();
        }

        // throw exception if response from pigpiod has not arrived yet
        if (in.available() < 16)
            throw new IOException("Timeout: No response from RPi withing "+ replyTimeout +" ms.");

        resp = Integer.reverseBytes(in.readInt()); // ignore response
        resp = Integer.reverseBytes(in.readInt()); // ignore response
        resp = Integer.reverseBytes(in.readInt()); // ignore response
        resp = Integer.reverseBytes(in.readInt()); // contains error or response
        return resp;
    }

    /**
     * Read all remaining bytes coming from pigpiod
     * @param data Array to store read bytes.
     * @throws IOException if unbale to read from network
     */
    public void readBytes(byte[] data) throws IOException {
        in.readFully(data);
    }

}
