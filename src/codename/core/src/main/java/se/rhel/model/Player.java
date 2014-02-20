package se.rhel.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import se.rhel.model.Entity.DynamicEntity;
import se.rhel.res.Resources;

public class Player extends DynamicEntity {

    private BulletWorld mWorld;

    public Player(Vector3 position, BulletWorld world) {
        super(position,
                new ModelInstance(Bodybuilder.INSTANCE.createBox(1f, 1f, 1f,
                        new Material(ColorAttribute.createDiffuse(Color.ORANGE))), position), 10f);

        mWorld = world;

        getInstance().transform.setTranslation(position);
        btCollisionShape playerShape = new btBoxShape(new Vector3(1f, 1f, 1f));
        btRigidBodyConstructionInfo playerInfo = new btRigidBodyConstructionInfo(1f, null, playerShape, Vector3.Zero);
        btDefaultMotionState playerMotionState = new btDefaultMotionState();
        playerMotionState.setWorldTransform(getInstance().transform);
        mWorld.addToWorld(playerShape, playerInfo, playerMotionState, getInstance());
    }

    public void update(float delta) {
    }


    @Override
    public void rotate(Vector3 axis, float angle) {
        super.rotate(axis, angle);
    }
}
