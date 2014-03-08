package se.rhel.packet;

/**
 * 0    = (byte) packet id
 * 1-4  = (int) player id
 */
public class ConnectPacket extends Packet {

    private static int PACKET_SIZE = Byte.SIZE + Integer.SIZE;
    public int mPlayerId;

    public ConnectPacket(int playerId) {
        super(PacketType.CONNECT, PACKET_SIZE);
        mBuffer.putInt(playerId);
    }

    public ConnectPacket(byte[] data) {
        super(data);

        mPlayerId = mBuffer.getInt();
    }



}
