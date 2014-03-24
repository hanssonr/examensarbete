package se.rhel.model.client;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import se.rhel.Client;
import se.rhel.model.*;
import se.rhel.network.packet.DamagePacket;
import se.rhel.network.packet.PlayerMovePacket;
import se.rhel.network.model.ExternalPlayer;
import se.rhel.network.packet.PlayerPacket;
import se.rhel.network.packet.RequestInitialStatePacket;
import se.rhel.observer.ClientControllerListener;
import se.rhel.observer.ClientListener;
import se.rhel.packet.Packet;
import se.rhel.packet.TestPacket;
import se.rhel.util.Log;

import java.util.ArrayList;


/**
 * Group: Mixed
 */
public class ClientWorldModel extends WorldModel implements ClientListener, ClientControllerListener {

    private Client mClient;
    private Array<ExternalPlayer> mPlayers;

    public ClientWorldModel(Client client) {
        super();

        mPlayers = new Array<>();
        mClient = client;
        mClient.addListener(this);
        mClient.sendTcp(new RequestInitialStatePacket(mClient.getId()));
    }

    @Override
    public void dispose() {

    }

    @Override
    public void update(float delta) {
        super.update(delta);

        for (int i = 0; i < mPlayers.size; i++) {
            mPlayers.get(i).update(delta);
        }
    }

    public ExternalPlayer getExternalPlayer(int id) {
        for (ExternalPlayer externalPlayer : mPlayers) {
            if(externalPlayer.getClientId() == id) {
                return externalPlayer;
            }
        }
        return null;
    }

    public Array<ExternalPlayer> getExternalPlayers() {
        return mPlayers;
    }

    @Override
    public void connected() {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void received(Object obj) {
        if (obj instanceof PlayerPacket) {
            Log.debug("ClientWorldModel", "Player_Join packet - Player can be viewed on client!!");
            PlayerPacket pp = (PlayerPacket)obj;
            ExternalPlayer ep = new ExternalPlayer(pp.clientId, new Vector3(pp.x, pp.y, pp.z), getBulletWorld());
            mPlayers.add(ep);
        }
        else if(obj instanceof TestPacket) {
            Log.debug("ClientWorldModel", "TestPacket received");
            TestPacket tp = (TestPacket)obj;
        }
        else if(obj instanceof PlayerMovePacket) {
            // An external player have moved and should be updated, accordingly
            PlayerMovePacket pmp = (PlayerMovePacket)obj;

            ExternalPlayer ep;
            synchronized (ep = getExternalPlayer(pmp.clientId)) {
                if(ep == null) {
                    return;
                }

                // Set the position & rotation
                ep.setPosition(pmp.pX, pmp.pY, pmp.pZ, pmp.rY, pmp.rW);
            }
        }
        else if (obj instanceof DamagePacket) {
            DamagePacket dp = (DamagePacket)obj;

            Log.debug("ClientWorldModel", "Received DamagePacket, Playerid: " + dp.mPlayerId + " got shot");
        }
        else if (obj instanceof ShootPacket) {
            // Visual representation of shoot
            Log.debug("ServerWorldModel", "ShotPacket received on client");
            ShootPacket sp = (ShootPacket)obj;

            getExternalPlayer(sp.clientId).shoot(sp.vFrom, sp.vTo, sp.vFrom2, sp.vTo2);
        }
    }

    @Override
    public void sendTCP(Packet packet) {
        Log.info("ClientWorldModel", "Input received from Controller - TCP send");
        // mClient.sendTcp();
    }

    @Override
    public void sendUDP(Packet packet) {
        Log.info("ClientWorldModel", "Input received from Controller - UDP send");
        // mClient.sendUdpFromServer();
    }
}