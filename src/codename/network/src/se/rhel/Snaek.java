package se.rhel;

import se.rhel.observer.ClientListener;

/**
 * Created by Emil on 2014-03-14.
 */
public class Snaek {
    public static final int PACKAGE_SIZE = 512;

    public static Server newServer(int tcpPort, int udpPort) {
        Server s = new Server();
        s.start();
        s.bind(tcpPort, udpPort);
        return s;
    }

    public static Client newClient(int tcpPort, int udpPort, String host) {
        Client c = new Client();
        c.start();
        c.connect(host, tcpPort, udpPort);
        return c;
    }

    public static Client newClient(int tcpPort, int udpPort, String host, ClientListener listener) {
        Client c = new Client();
        c.addListener(listener);
        c.start();
        c.connect(host, tcpPort, udpPort);
        return c;
    }

}
