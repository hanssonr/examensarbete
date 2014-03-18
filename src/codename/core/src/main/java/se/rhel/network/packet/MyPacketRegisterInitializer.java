package se.rhel.network.packet;

import se.rhel.packet.PacketManager;
import se.rhel.packet.TestMaxPacket;
import se.rhel.packet.TestPacket;

/**
 * Group: Network
 *
 * Created by rkh on 2014-03-12.
 */
public class MyPacketRegisterInitializer {

    public static void register() {
        PacketManager.getInstance().registerPacket(PlayerPacket.class);
        PacketManager.getInstance().registerPacket(RequestInitialStatePacket.class);
        PacketManager.getInstance().registerPacket(TestPacket.class);
        PacketManager.getInstance().registerPacket(TestMaxPacket.class);
        PacketManager.getInstance().registerPacket(PlayerMovePacket.class);
    }
}