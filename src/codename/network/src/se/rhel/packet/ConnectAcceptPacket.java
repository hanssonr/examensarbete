package se.rhel.packet;

/**
 * Created by rkh on 2014-03-04.
 */
public class ConnectAcceptPacket extends Packet {

    public ConnectAcceptPacket(int id) {
        super(1, 10);

        System.out.println("ID" + id);
        mBuffer.putInt(id);

    }
}
