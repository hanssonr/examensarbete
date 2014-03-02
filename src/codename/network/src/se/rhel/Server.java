package se.rhel;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Server implements EndPoint {

    public static String NAME;
    private static int PORT;

    private DatagramSocket mSocket;
    private BufferedReader mIn;

    public Server(String name, int port) throws SocketException {
        PORT = port;
        NAME = name;
        mSocket = new DatagramSocket(PORT);
    }

    @Override
    public void run() {
        try {
            byte[] buf = new byte[256];

            // Recieve request
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            mSocket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Debug > Received on server: " + received);

            // Response
            String responseString = "hellu";
            buf = responseString.getBytes();

            // Send response
            InetAddress address = packet.getAddress();
            int port = packet.getPort();

            if(port == PORT) {
                packet = new DatagramPacket(buf, buf.length, address, port);
                mSocket.send(packet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        run();
    }

    @Override
    public void stop() {
        mSocket.close();
    }

}
