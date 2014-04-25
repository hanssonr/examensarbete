package se.rhel.model.component;

/**
 * Created by rkh on 2014-04-24.
 */
public interface IActionable {

    public void shoot();
    public boolean canThrowGrenade();
    public void update(float delta);
    public boolean hasShoot();
}
