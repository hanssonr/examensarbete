package se.rhel;

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
                    EmilClient c = new EmilClient("Emil-PC", 4455);
                    new Thread(c).start();
                }

            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }
}
