package se.rhel.screen.network;

import com.badlogic.gdx.math.Vector3;
import se.rhel.Server;
import se.rhel.event.EventHandler;
import se.rhel.event.EventType;
import se.rhel.event.ModelEvent;
import se.rhel.event.ModelListener;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.model.weapon.Grenade;
import se.rhel.network.packet.GrenadeCreatePacket;

/**
 * Group: Mixed
 * Created by Emil on 2014-04-02.
 */
public class ServerController implements ModelListener {

    private Server mServer;
    private ServerWorldModel mServerWorldModel;

    public ServerController(Server server) {
        mServer = server;
        mServerWorldModel = new ServerWorldModel(server);

        // Listen to pure ServerModelEvents
        EventHandler.events.listen(ModelEvent.class, this);
    }

    public void update(float delta) {
        mServerWorldModel.update(delta);
    }

    @Override
    public void modelEvent(EventType type, Object... objs) {
        switch (type) {
            default:
                break;
        }
    }
}
