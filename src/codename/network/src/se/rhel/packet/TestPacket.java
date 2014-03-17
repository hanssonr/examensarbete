package se.rhel.packet;

/**
 * Ska inte räknas!!!!
 *
 * Created by Emil on 2014-03-14.
 */
public class TestPacket extends Packet {

    public int clientId;
    public float floatVal;
    public double doubleVal;
    public char charVal;
    public byte byteVal;
    public long longVal;
    public short shortVal;

    public TestPacket() {}

    public TestPacket(int clientId, float fVal, double dVal, char cVal, byte bVal, long lVal, short sVal) {
        super(TestPacket.class);
        putInt(clientId);
        putFloat(fVal);
        putDouble(dVal);
        putChar(cVal);
        putByte(bVal);
        putLong(lVal);
        putShort(sVal);

        ready();
    }

    public TestPacket(byte[] data) {
        super(data);
        clientId = getInt();
        floatVal = getFloat();
        doubleVal = getDouble();
        charVal = getChar();
        byteVal = getByte();
        longVal = getLong();
        shortVal = getShort();
    }
}
