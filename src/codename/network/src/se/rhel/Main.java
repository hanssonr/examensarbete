package se.rhel;

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
