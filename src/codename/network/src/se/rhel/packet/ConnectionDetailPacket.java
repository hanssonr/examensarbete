package se.rhel.packet;

public class ConnectionDetailPacket extends Packet {

    public int mPlayerId;
    public int mUdpPort;

    public ConnectionDetailPacket() {}

    public ConnectionDetailPacket(int playerId, int udpPort) {
        super(ConnectionDetailPacket.class);
        super.putInt(playerId);
        super.putInt(udpPort);
        super.ready();
    }

    public ConnectionDetailPacket(byte[] data) {
        super(data);

        mPlayerId = super.getInt();
        mUdpPort = super.getInt();
    }
}
