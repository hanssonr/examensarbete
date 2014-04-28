package se.rhel.model.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import se.rhel.model.physics.BulletWorld;

import java.util.ArrayList;


/**
 * Group: Mixed
 */
public class GameObject implements ITransform {

    private int ID;
    public int getId() { return ID; }
    public void setId(int id) { ID = id; }
    protected ITransform mTransform;

    public ArrayList<IComponent> mComponents = new ArrayList<>();

    public GameObject() {
        mTransform = createTransformComponent();
    }

    public boolean hasComponent(Class<?> classtype) {
        for(IComponent component : mComponents) {
            if(classtype.isInstance(component)) return true;
        }
        return false;
    }

    /**
     * Returns a component
     * @param componentClass - Class of component
     * @return
     */
    public IComponent getComponent(Class<?> componentClass) {
        for(IComponent component : mComponents) {
            if(componentClass == component.getClass()) {
                return component;
            }
        }

        return null;
    }

    private void addComponent(IComponent component) {
        mComponents.add(component);
    }

    protected IPhysics createPhysicsComponent(BulletWorld world) {
        PhysicsComponent pc = new PhysicsComponent(world);
        addComponent(pc);
        return pc;
    }

    protected IActionable createActionComponent() {
        ActionComponent ac = new ActionComponent();
        addComponent(ac);
        return ac;
    }

    protected IDamageable createDamageableComponent(int maxhealth) {
        DamageComponent dc = new DamageComponent(maxhealth);
        addComponent(dc);
        return dc;
    }

    protected IGravity createGravityComponent(btCollisionWorld collisionworld, float gravitypower) {
        GravityComponent gc = new GravityComponent(collisionworld, gravitypower);
        addComponent(gc);
        return gc;
    }

    protected INetwork createNetworkComponent(int id) {
        NetworkComponent nc = new NetworkComponent(id);
        addComponent(nc);
        return nc;
    }

    private ITransform createTransformComponent() {
        TransformComponent tc = new TransformComponent();
        addComponent(tc);
        return tc;
    }

    public Matrix4 getTransformation() {
        return mTransform.getTransformation();
    }

    @Override
    public Vector3 getPosition() {
        return mTransform.getPosition();
    }

    @Override
    public Vector3 getDirection() {
        return mTransform.getDirection();
    }

    @Override
    public Vector3 getRotation() {
        return mTransform.getRotation();
    }

    @Override
    public void rotateBy(Vector3 amount) {
        mTransform.rotateBy(amount);
    }

    @Override
    public void rotateTo(Vector3 rotation) {
        mTransform.rotateTo(rotation);
    }

    @Override
    public void rotateAndTranslate(Vector3 rotation, Vector3 position) {
        mTransform.rotateAndTranslate(rotation, position);
    }

    public void destroy() {
        if(hasComponent(IPhysics.class)) {
            IPhysics pc = (IPhysics) getComponent(PhysicsComponent.class);
            pc.getPhysicsWorld().getCollisionWorld().removeCollisionObject(pc.getBody());
        }
    }
}
