package se.rhel.packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PacketManager {

    private static PacketManager INSTANCE;
    private int UNIQUE_ID = -127;
    private HashMap<Integer, Class<?>> PACKETS = new HashMap<>();

    public static PacketManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new PacketManager();

        return INSTANCE;
    }

    public void registerPacket(Class<?> packet) {
        PACKETS.put((int)generateUniqueId(), packet);
    }

    public synchronized Class<?> getPacketType(int id) {
        if (PACKETS.containsKey(id)) {
            return PACKETS.get(id);
        } else {
            System.out.println("NO PACKET WITH ID " + id + " UNIQUE ID " + UNIQUE_ID);
            System.exit(1);
        }
        return null;
    }

    public synchronized int getPacketId(Class<?> type) {
        Iterator it = PACKETS.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Integer, Class<?>> pairs = (Map.Entry)it.next();
            if (pairs.getValue().equals(type)) {
                return pairs.getKey();
            }
        }

        throw new IllegalArgumentException("No packet " + type + " registered");
    }

    private synchronized byte generateUniqueId() {
        int id = UNIQUE_ID++;
        if(id > 127) throw new IndexOutOfBoundsException("Cant exceed 256 packets");
        return (byte)id;
    }
}
