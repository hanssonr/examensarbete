package se.rhel.network.packet;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.model.entity.IPlayer;
import se.rhel.packet.Packet;

import java.util.ArrayList;

/**
 * Group: Multiplayer
 *
 * Size: 35 bytes * number of players (MAX 18)
 */
public class PlayerPacket extends Packet {

    public ArrayList<UpdateStruct> mPlayers = new ArrayList<>();

    public PlayerPacket(Array<IPlayer> players) {
        super(PlayerPacket.class);

        putInt(players.size);
        for(int i = 0; i < players.size; i++) {
            IPlayer player = players.get(i);
            addData(i+1, player.getPosition(), player.getRotation());
        }
        super.ready();
    }

    public PlayerPacket(int clientId, Vector3 position, Vector3 rotation) {
        super(PlayerPacket.class);
        super.putInt(1);

        addData(clientId, position, rotation);
        super.ready();
    }

    private void addData(int clientId, Vector3 position, Vector3 rotation) {
        super.putInt(clientId);
        super.putFloat(position.x);
        super.putFloat(position.y);
        super.putFloat(position.z);
        super.putFloat(rotation.x);
        super.putFloat(rotation.y);
        super.putFloat(rotation.z);
    }

    public PlayerPacket(byte[] data) {
        super(data);
        int x = super.getInt();
        for(int i = 0; i < x; i++) {
            int id = getInt();
            Vector3 pos = new Vector3(getFloat(), getFloat(), getFloat());
            Vector3 rot = new Vector3(getFloat(), getFloat(), getFloat());
            mPlayers.add(new UpdateStruct(id, pos, rot));
        }
    }
}
