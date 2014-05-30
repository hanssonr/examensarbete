package se.rhel.network.packet;

import se.rhel.packet.PacketManager;
import se.rhel.packet.TestMaxPacket;
import se.rhel.packet.TestPacket;

/**
 * Group: Multiplayer
 * Created by rkh on 2014-03-12.
 */
public class MyPacketRegisterInitializer {

    public static void register() {
        PacketManager.getInstance().registerPacket(PlayerPacket.class);
        PacketManager.getInstance().registerPacket(RequestInitialStatePacket.class);
        PacketManager.getInstance().registerPacket(TestPacket.class);
        PacketManager.getInstance().registerPacket(TestMaxPacket.class);
        PacketManager.getInstance().registerPacket(PlayerMovePacket.class);
        PacketManager.getInstance().registerPacket(ShootPacket.class);
        PacketManager.getInstance().registerPacket(DamagePacket.class);
        PacketManager.getInstance().registerPacket(BulletHolePacket.class);
        PacketManager.getInstance().registerPacket(DeadEntityPacket.class);
        PacketManager.getInstance().registerPacket(GrenadeCreatePacket.class);
        PacketManager.getInstance().registerPacket(GrenadeUpdatePacket.class);
        PacketManager.getInstance().registerPacket(ConnectedPacket.class);
        PacketManager.getInstance().registerPacket(RespawnPacket.class);
    }
}
