package se.rhel;

/**
 * Created by Emil on 2014-03-14.
 */
public class Snaek {
    public static final int PACKAGE_SIZE = 128;

    public static Server newServer(int tcpPort, int udpPort) {
        Server s = new Server();
        s.start();
        s.bind(tcpPort, udpPort);
        return s;
    }

    public static Client newClient(int tcpPort, int udpPort, String address) {
        Client c = new Client();
        c.start();
        c.connect(address, tcpPort, udpPort);
        return c;
    }

}
