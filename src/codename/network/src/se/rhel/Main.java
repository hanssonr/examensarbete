package se.rhel;

import se.rhel.packet.PacketHandler;

import java.io.IOException;
import java.net.SocketException;

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
                    Client c = new Client(new PacketHandler());
                    c.connect("127.0.0.1", 4455);
                }

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
