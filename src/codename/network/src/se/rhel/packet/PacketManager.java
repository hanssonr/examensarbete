package se.rhel.packet;

import se.rhel.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PacketManager {

    private static PacketManager INSTANCE;
    private int UNIQUE_ID = -121;

    private static int[] RESERVED_IDS = new int[] { -127, -126, -125, -124, -123, -122 };
    private static int pointer = 0;
    private static boolean isIdsAvailable = true;

    private HashMap<Integer, Class<?>> PACKETS = new HashMap<>();

    public static PacketManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new PacketManager();

        return INSTANCE;
    }

    public synchronized void registerPacket(Class<?> packet) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        // If the caller is server, use the reserved ids
        if(stackTraceElements[2].getClassName().equals(PacketRegisterInitializer.class.getName())) {
            if(isIdsAvailable) {
                PACKETS.put(RESERVED_IDS[pointer], packet);
                Log.debug("PacketManager", "        > ID: " + RESERVED_IDS[pointer] + ", Packet: " + packet.getName());
                pointer++;

                if(pointer == RESERVED_IDS.length) {
                    isIdsAvailable = false;
                }
            } else {
                Log.error("PacketManager", "No more reserved ids available!");
            }

        } else {
            int id = (int)generateUniqueId();
            System.out.println("        > ID: " + id + ", Packet: " + packet.getName());
            Log.debug("PacketManager", "        > ID: " + id + ", Packet: " + packet.getName());
            PACKETS.put(id, packet);
        }
    }

    public synchronized Class<?> getPacketType(int id) {
        return PACKETS.get(id);
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
