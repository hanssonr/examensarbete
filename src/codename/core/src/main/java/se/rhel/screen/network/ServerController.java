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
            case GRENADE:
                // Now it should be alright to throw a grenade
                Vector3 pos = (Vector3) objs[0];
                Vector3 dir = (Vector3) objs[1];

                // A player has thrown from the model
                Grenade g = mServerWorldModel.addGrenade(pos, dir);
                mServer.sendToAllTCP(new GrenadeCreatePacket(g.getId(), pos, dir));
                // mClient.sendTcp(new GrenadeCreatePacket(g.getId(), pos, dir));

                // mClientWorldModel.addGrenade(pos, dir);
                // mWorldView.getGrenadeRenderer().addGrenade(mClientWorldModel.getGrenades().get(mClientWorldModel.getGrenades().size()-1));
                break;
            default:
                break;
        }
    }
}
