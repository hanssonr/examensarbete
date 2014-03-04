package se.rhel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) throws UnknownHostException {

        if(args.length != 1) {
            assert false;
        } else {
            try {

                if(args[0].equals("S")) {
                    Server server = new Server("DaServer", 4455);
                    server.start();
                } else {
//                    Client c = new Client("Emil-PC", 4455);
//                    c.start();

                    Client c = new Client();
                    c.start();
                    c.connect(InetAddress.getByName("localhost"), 4455);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
