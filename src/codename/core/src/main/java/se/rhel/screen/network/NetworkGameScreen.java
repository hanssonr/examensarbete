package se.rhel.screen.network;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import se.rhel.Client;
import se.rhel.CodeName;
import se.rhel.Snaek;
import se.rhel.model.FPSCamera;
import se.rhel.model.physics.MyContactListener;
import se.rhel.network.packet.PlayerMovePacket;
import se.rhel.network.packet.ShootPacket;
import se.rhel.screen.BaseScreen;
import se.rhel.Server;
import se.rhel.view.BulletHoleRenderer;
import se.rhel.view.input.PlayerInput;
import se.rhel.model.client.ClientWorldModel;
import se.rhel.model.server.ServerWorldModel;
import se.rhel.view.WorldView;

import java.lang.reflect.Array;

/**
 * Group: Mixed
 */
public class NetworkGameScreen extends BaseScreen {

    private PlayerInput mPlayerInput;
    private WorldView mWorldView;

    private Server mServer;
    private ServerWorldModel mServerWorldModel;
    private boolean mUpdateServer = false;

    private Client mClient;
    private ClientWorldModel mClientWorldModel;
    private Vector3 mLastKnownPosition = Vector3.Zero;
    private float mLastKnownRotation = 0f;

    public NetworkGameScreen(CodeName game, Server server) {
        super(game);
        Gdx.app.setLogLevel(Application.LOG_NONE);

        mServer = server;

        if(mServer != null) {
            mServerWorldModel = new ServerWorldModel(server);
            mUpdateServer = true;
        }

        mClient = Snaek.newClient(4455, 5544, "localhost");
        mClientWorldModel = new ClientWorldModel(mClient);
        //mClientWorldModel.create();

        mPlayerInput = new PlayerInput();
        mWorldView = new WorldView(mClientWorldModel);

        Gdx.input.setInputProcessor(mPlayerInput);
    }


    @Override
    public void update(float delta) {
        mPlayerInput.processCurrentInput(delta);

        //mClientWorldModel.getPlayer().rotate(mPlayerInput.getRotation());

//        mVelocity.add(mCamera.getForward().scl(direction.z * mMovespeed));
//        mVelocity.add(mCamera.getRight().scl(direction.x * mMovespeed));

        mClientWorldModel.getPlayer().move(mPlayerInput.getDirection());
        mClientWorldModel.getPlayer().rotate(mPlayerInput.getRotation());

        mWorldView.getCamera().rotate(mPlayerInput.getRotation());

//        if (mPlayerInput.isShooting()) {
//            Vector3[] rays = mClientWorldModel.getPlayer().shoot();
//            Vector3[] visVerts = mClientWorldModel.getPlayer().getVisualRepresentationShoot();
//
//            // Check shoot collision local
//            MyContactListener.CollisionObject co = MyContactListener.checkShootCollision(mClientWorldModel.getBulletWorld().getCollisionWorld(), rays);
//            // If we have hit the world, just draw a bullethole (it doesn't matter if the server says otherwise)
//            if(co != null && co.type == MyContactListener.CollisionObject.CollisionType.WORLD) {
//                // Draw bullethole
//                BulletHoleRenderer.addBullethole(co.hitPoint, co.hitNormal);
//            }
//
//            if(rays != null) {
//                // Also notify the server that we have shot
//                mClient.sendTcp(new ShootPacket(mClient.getId(), rays[0], rays[1], visVerts[0], visVerts[1], visVerts[2], visVerts[3]));
//            }
//        }

        if (mPlayerInput.isJumping())
            mClientWorldModel.getPlayer().jump();

        mClientWorldModel.update(delta);
        if(mUpdateServer) mServerWorldModel.update(delta);

        // Network stuff
        if(mClient.getId() != -1) {
            // Send move packet

            if(mClientWorldModel.getPlayer().getPosition().dst(mLastKnownPosition) > 0.01f ||
                    mClientWorldModel.getPlayer().getRotation() != mLastKnownRotation)  {
                mLastKnownPosition = mClientWorldModel.getPlayer().getPosition().cpy();
                mLastKnownRotation = mClientWorldModel.getPlayer().getRotation();

                mClient.sendUdp(
                        new PlayerMovePacket(mClient.getId(),
                        mClientWorldModel.getPlayer().getPosition(), mClientWorldModel.getPlayer().getRotation()));
            }
        }

        mClientWorldModel.update(delta);
        mWorldView.update(delta);
    }

    @Override
    public void draw(float delta) {
        mWorldView.render(delta);
    }

    @Override
    public void dispose() {
        super.dispose();

        mWorldView.dispose();
    }
}
