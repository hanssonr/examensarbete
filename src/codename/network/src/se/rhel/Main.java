package se.rhel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {

        if(args.length != 1) {
            assert false;
        } else {
            try {

                if(args[0].equals("S")) {
                    Server server = new Server("DaServer", 4455);
                    server.start();// new Thread(server).start(); // server.start();
                } else {
                    //EmilClient c = new EmilClient("Emil-PC", 4455);
                    //new Thread(c).start();
                    Client c = new Client();
                    c.connect(InetAddress.getByName("192.168.0.101"), 4455);
                    c.start();
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
