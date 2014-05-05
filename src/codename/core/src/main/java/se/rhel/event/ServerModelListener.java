package se.rhel.event;

import com.badlogic.gdx.math.Vector3;
import se.rhel.model.component.GameObject;
import se.rhel.model.entity.IPlayer;
import se.rhel.model.entity.Player;
import se.rhel.model.physics.RayVector;
import se.rhel.model.weapon.Grenade;

/**
 * Created by Emil on 2014-04-21.
 */
public interface ServerModelListener {
    public void gameObjectEvent(EventType type, GameObject gObj);
    public void serverWorldCollision(Vector3 hitPoint, Vector3 hitNormal);
    public void shootEvent(RayVector ray, IPlayer player);
    public void grenadeEvent(Grenade g, boolean isAlive);
}
