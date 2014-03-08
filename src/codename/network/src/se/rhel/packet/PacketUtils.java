package se.rhel.packet;

import java.util.HashMap;

/**
 * Created by rkh on 2014-03-08.
 */

public class PacketUtils {

    private static PacketUtils INSTANCE = null;
    private static HashMap<Packet.PacketType, Byte> PACKETS = new HashMap<>();
    private static int UNIQUE_ID = -127;

    // Private constructor
    private PacketUtils() {
        for(Packet.PacketType type : Packet.PacketType.values()) {
            PACKETS.put(type, generateUniqueId());
        }
    }

    public static synchronized PacketUtils getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new PacketUtils();
        }
        return INSTANCE;
    }

    private synchronized byte generateUniqueId() {
        int id = UNIQUE_ID++;
        if(id > 127) throw new IndexOutOfBoundsException("Cant exceed 256 packets");
        return (byte)id;
    }

    public Packet.PacketType getPacketType(byte id) {
        for(Packet.PacketType type : PACKETS.keySet()) {
            if (id == PACKETS.get(type)) {
                return type;
            }
        }

        return null;
    }

        public byte getPacketId(Packet.PacketType type) {
        return PACKETS.get(type);
    }

}

