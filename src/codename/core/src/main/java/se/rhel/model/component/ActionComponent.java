package se.rhel.model.component;

/**
 * Created by Emil on 2014-04-01.
 */
public class ActionComponent implements IActionable, IComponent {

    // Laser
    private boolean mHasShot = false;
    private boolean mCanShoot = true;
    private float mDeltaShoot;

    // Grenades
    private final int MAX_GRENADES = 10;
    private int currentGrenades = 0;

    public void update(float delta) {
        // Update shooting for unnecessary drawing / spam shooting
        if(!mCanShoot) {
            mDeltaShoot += delta;
            if(mDeltaShoot > 0.4f) {
                mCanShoot = true;
                mDeltaShoot = 0f;
            }
        }
    }

    @Override
    public boolean hasShoot() {
        boolean ret = mHasShot;
        mHasShot = false;
        return ret;
    }

    public void shoot() {
        // If can shoot
        if(mCanShoot) {
            mHasShot = true;
            mCanShoot = false;
        }
    }

    public boolean canThrowGrenade() {
        if(currentGrenades < MAX_GRENADES) {
            currentGrenades++;
            return true;
        }

        return false;
    }
}
