package se.rhel.packet;

/**
 * Created by rkh on 2014-03-12.
 */
public class PacketRegisterInitializer {

    private static boolean isRegistered = false;

    public static void register() {
        if(isRegistered) return;

        PacketManager.getInstance().registerPacket(IdlePacket.class);
        PacketManager.getInstance().registerPacket(DisconnectPacket.class);
        PacketManager.getInstance().registerPacket(ConnectionDetailPacket.class);
        PacketManager.getInstance().registerPacket(HandshakeResponsePacket.class);
        PacketManager.getInstance().registerPacket(LatencyPacket.class);
        PacketManager.getInstance().registerPacket(IdlePacket.class);

        isRegistered = true;
    }

}
