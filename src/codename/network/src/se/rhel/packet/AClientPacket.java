package se.rhel.packet;

/**
 * Every user created packet should extend this class
 * and override the three constructors.
 */
public abstract class AClientPacket extends Packet {

    public int clientId = -1;

    /**
     * Empty constructor for creation and instanceof check
     */
    public AClientPacket() {}

    /**
     * Creating a new packet from a client
     * @param clientId the clients Id
     * @param classType can be ignored as parameter but must be super()-called
     */
    public AClientPacket(int clientId, Class classType) {
        super(classType);
        putInt(clientId);
    }

    /**
     * Generates back the class from a byte-array
     * @param data
     */
    public AClientPacket(byte[] data) {
        super(data);
        clientId = getInt();
    }

}