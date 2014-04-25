package se.rhel.view;

import com.badlogic.gdx.math.Vector3;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;

/**
 * Created by rkh on 2014-04-17.
 */
public interface IWorldView {

    public FPSCamera getCamera();


    public void addGrenade(Grenade grenade);
    public void addPlayer(IPlayer player);
    public void addParticleEffect(Vector3 position, ParticleRenderer.Particle type);
    public void shoot(RayVector ray);
    public void update(float delta);
    public void render(float delta);
    public void dispose();

    public void addBullethole(Vector3 position, Vector3 normal);
}
