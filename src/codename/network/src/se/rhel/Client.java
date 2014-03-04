package se.rhel;

import se.rhel.packet.ConnectPacket;
import se.rhel.packet.MovePacket;
import se.rhel.packet.Packet;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class Client implements EndPoint {

    private DatagramSocket mUdpSocket;
    private DatagramPacket mUdpPacket;

    private Thread mUpdateThread;

    private InetAddress mHost;
    private int mPort;

    private byte[] mReceiveBuffer;

    private boolean mShouldRun;
    private boolean mIsSocketConnected = false;

    public Client() {
        this(1024);
    }

    public Client(int receiveBufferSize) {
        mReceiveBuffer = new byte[receiveBufferSize];
    }

    public void connect(InetAddress host, int port) throws IOException {
        System.out.println("connecting");
        mHost = host;
        mPort = port;

        mUdpSocket = new DatagramSocket();
        mUdpSocket.connect(host, port);
        mIsSocketConnected = true;

        mUdpPacket = new DatagramPacket(mReceiveBuffer, mReceiveBuffer.length);
        sendPacket(new ConnectPacket());
    }

    private void update() throws IOException {
        if(!mIsSocketConnected) return;

        DatagramPacket receivePacket = new DatagramPacket(mReceiveBuffer, mReceiveBuffer.length);
        mUdpSocket.receive(receivePacket);
    }

    public void sendPacket(Packet packet) throws IOException {
        System.out.println("CLIENT::sendPacket > " + packet);
        mUdpPacket = new DatagramPacket(packet.getData(), packet.getData().length, mHost, mPort);
        mUdpSocket.send(mUdpPacket);
    }


    @Override
    public void run() {
        while(mShouldRun) try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        if(mUpdateThread == null) {
            mShouldRun = true;
            mUpdateThread = new Thread(this, "Client");
            mUpdateThread.start();
        }
    }

    @Override
    public void stop() {
        if(mShouldRun) return;
        mShouldRun = false;
        mUdpSocket.close();
    }
}

///**
// * Created by Emil on 2014-02-27.
// * assigned to libgdx-gradle-template in se.rhel
// */
//public class Client implements EndPoint {
//
//    private String mHostName;
//    private boolean mConnected;
//    private DatagramSocket mSocket;
//    private int mPort;
//
//    public Client(String host, int port) throws SocketException {
//        mHostName = host;
//        mSocket = new DatagramSocket();
//        mPort = port;
//    }
//
//    @Override
//    public void run() {
//
//        // Just some dummy input
//        System.out.print("Enter chatmessage to server: ");
//        Scanner scanner = new Scanner(System.in);
//        String read = scanner.nextLine();
//
//        // Send request
//        byte[] rec = new byte[256];
//        byte[] buf = read.getBytes();
//
//        try {
//            InetAddress address = InetAddress.getByName(mHostName);
//            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, mPort);
//            mSocket.send(packet);
//
//            // Get response
//            packet = new DatagramPacket(rec, rec.length);
//            mSocket.receive(packet);
//
//            String received = new String(packet.getData(), 0, packet.getLength());
//            System.out.println("Received from server: " + received);
//
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void start() {
//        run();
//    }
//
//    @Override
//    public void stop() {
//        mSocket.close();
//    }
//}
