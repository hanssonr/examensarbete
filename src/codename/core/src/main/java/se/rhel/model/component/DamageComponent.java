package se.rhel.model.component;


/**
 * Group: Logic
 *
 * Created by rkh on 2014-03-24.
 */
public class DamageComponent implements IDamageable, IComponent {

    private boolean mAlive = true;
    private int mHealth;

    public DamageComponent(int maxHealth) {
        mHealth = maxHealth;
    }

    public boolean isAlive() {
        return mAlive;
    }

    public int getHealth() {
        return mHealth;
    }

    public void damageEntity(int amount) {
        mHealth -= amount;
    }

    public void setAlive(boolean b) {
        mAlive = b;
    }
}
