package se.rhel;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Client implements EndPoint {

    private String mHostName;
    private boolean mConnected;
    private DatagramSocket mSocket;
    private int mPort;

    public Client(String host, int port) throws SocketException {
        mHostName = host;
        mSocket = new DatagramSocket();
        mPort = port;
    }

    @Override
    public void run() {

        // Just some dummy input
        System.out.print("Enter chatmessage to server: ");
        Scanner scanner = new Scanner(System.in);
        String read = scanner.nextLine();

        // Send request
        byte[] rec = new byte[256];
        byte[] buf = read.getBytes();

        try {
            InetAddress address = InetAddress.getByName(mHostName);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, mPort);
            mSocket.send(packet);

            // Get response
            packet = new DatagramPacket(rec, rec.length);
            mSocket.receive(packet);

            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received from server: " + received);

        } catch (UnknownHostException e) {
            e.printStackTrace();
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
