package se.rhel.packet;

/**
 * Created by rkh on 2014-03-05.
 */
public interface IPacketHandler {
    public void handlePacket(byte[] data);
}
