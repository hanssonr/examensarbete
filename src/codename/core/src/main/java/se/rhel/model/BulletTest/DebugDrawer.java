package se.rhel.model.BulletTest;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/** @author xoppa */
public class DebugDrawer extends btIDebugDraw {
    public int debugMode = 0;
    public ShapeRenderer lineRenderer = new ShapeRenderer();

    @Override
    public void drawLine (btVector3 from, btVector3 to, btVector3 color) {
        lineRenderer.setColor(color.getX(), color.getY(), color.getZ(), 1f);
        lineRenderer.line(from.getX(), from.getY(), from.getZ(), to.getX(), to.getY(), to.getZ());
    }


    @Override
    public void drawContactPoint (btVector3 PointOnB, btVector3 normalOnB, float distance, int lifeTime, btVector3 color) {
    }

    @Override
    public void reportErrorWarning (String warningString) {
    }

    @Override
    public void draw3dText (btVector3 location, String textString) {
    }

    @Override
    public void setDebugMode (int debugMode) {
        this.debugMode = debugMode;
    }

    @Override
    public int getDebugMode () {
        return debugMode;
    }

    public void begin() {
        lineRenderer.begin(ShapeRenderer.ShapeType.Line);
    }

    public void end() {
        lineRenderer.end();
    }
}
