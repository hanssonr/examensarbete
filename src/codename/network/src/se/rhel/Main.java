package se.rhel;

import se.rhel.client.Client;
import se.rhel.client.ClientPacketHandler;
import se.rhel.server.Server;

import java.io.IOException;
import java.net.SocketException;

public class Main {

    public static void main(String[] args) {

        if(args.length != 1) {
            assert false;
        } {
            if (args[0].equals("S")) {
                Server server = new Server();
                server.start();
                server.bind(4455, 5544);
            } else {
                Client client = new Client();
                client.start();
                client.connect("127.0.0.1", 4455, 5544);
            }
        }
    }
}
