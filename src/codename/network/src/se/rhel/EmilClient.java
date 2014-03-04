package se.rhel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class EmilClient implements Runnable {

    private String mHostName;
    private boolean mConnected;
    private DatagramSocket mSocket;
    private int mPort;

    public EmilClient(String host, int port) throws SocketException {
        mHostName = host;
        mSocket = new DatagramSocket();
        mPort = port;
    }

    @Override
    public void run() {

        while(true) {
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
                /*packet = new DatagramPacket(rec, rec.length);
                mSocket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received from server: " + received);*/

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (
                    Socket kkSocket = new Socket(mHostName, mPort);

                    PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(kkSocket.getInputStream()));
            ) {
                BufferedReader stdIn =
                        new BufferedReader(new InputStreamReader(System.in));
                String fromServer;
                String fromUser;

                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server: " + fromServer);
                    if (fromServer.equals("Bye."))
                        break;

                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        System.out.println("EmilClient: " + fromUser);
                        out.println(fromUser);
                    }
                }
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + mHostName);
                System.exit(1);
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + mHostName);
                System.exit(1);
            }
        }
    }
}
