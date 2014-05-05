package se.rhel.model.component;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionWorld;
import se.rhel.model.physics.BulletWorld;

import java.util.ArrayList;


/**
 * Group: Mixed
 */
public class GameObject implements ITransform, IUpdateable {

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
     * Returns a component that either implements a interface or are of the class type that param is.
     * Don't put a generic interface e.g IComponent, then You are a tard.
     *
     * @param componentClass - Class of component
     * @return
     */
    public IComponent getComponent(Class<?> componentClass) {
        for(IComponent component : mComponents) {
            if(componentClass.equals(component.getClass())) {
                return component;
            }

            for(Class classinterface : component.getClass().getInterfaces()) {
                if(classinterface.equals(componentClass))
                    return component;
            }
        }

        return null;
    }

    public void addComponent(IComponent component) {
        if(!hasComponent(component.getClass()))
            mComponents.add(component);
    }

    @Override
    public void update(float delta) {
        for(IComponent component : mComponents) {
            if(component instanceof IUpdateable) {
                ((IUpdateable)component).update(delta);
            }
        }
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

    protected IGravity createGravityComponent(btCollisionWorld collisionworld, IPhysics physicscomponent, float gravitypower) {
        GravityComponent gc = new GravityComponent(collisionworld, physicscomponent, gravitypower);
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
