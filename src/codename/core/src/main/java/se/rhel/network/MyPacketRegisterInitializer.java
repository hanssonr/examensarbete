package se.rhel.network;

import se.rhel.packet.PacketManager;

/**
 * Created by rkh on 2014-03-12.
 */
public class MyPacketRegisterInitializer {

    public static void register() {
        PacketManager.getInstance().registerPacket(PlayerPacket.class);
        PacketManager.getInstance().registerPacket(RequestInitialStatePacket.class);
    }
}
