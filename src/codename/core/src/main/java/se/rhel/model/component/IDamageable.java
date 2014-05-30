package se.rhel.model.component;

/**
 * Created by rkh on 2014-04-24.
 */
public interface IDamageable {

    public boolean isAlive();
    public int getHealth();
    public void damageEntity(int amount);
    public void setAlive(boolean b);
    public void reset();
}
