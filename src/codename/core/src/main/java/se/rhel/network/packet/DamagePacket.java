package se.rhel.network.packet;

import se.rhel.packet.Packet;

/**
 * Created by rkh on 2014-03-21.
 */
public class DamagePacket extends Packet {

    public int mPlayerId;

    public DamagePacket() {}

    public DamagePacket(int playerId) {
        super(DamagePacket.class);

        putInt(playerId);

        super.ready();
    }

    public DamagePacket(byte[] data) {
        super(data);

        mPlayerId = getInt();
    }
}
