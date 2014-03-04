package se.rhel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class TcpConnection implements Runnable {

    private Socket mSocket;

    public TcpConnection(Socket socket) {
        mSocket = socket;
    }

    @Override
    public void run() {
        try (
                PrintWriter out = new PrintWriter(mSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
        ) {
            System.out.println("TCPAddress: " + mSocket.getInetAddress() + " TCPPort: " + mSocket.getPort() + " TCPLocalPort: " + mSocket.getLocalPort());
            String inputLine, outputLine;
            out.println("From tcp");

            while ((inputLine = in.readLine()) != null) {
                outputLine = "From2 tcp";
                out.println(outputLine);
                if (outputLine.equals("Bye"))
                    break;
            }
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
    }
}
