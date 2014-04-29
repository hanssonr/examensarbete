package se.rhel.network.packet;

import se.rhel.packet.AClientPacket;

/**
 * Group: Multiplayer
 * Created by rkh on 2014-03-21.
 */
public class DamagePacket extends AClientPacket {

    public int amount;

    public DamagePacket(int playerId, int amount) {
        super(playerId, DamagePacket.class);
        putInt(amount);
        super.ready();
    }

    public DamagePacket(byte[] data) {
        super(data);
        amount = getInt();
    }
}
