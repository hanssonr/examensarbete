package se.rhel;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public interface EndPoint extends Runnable {
    public void run();
    public void start();
    public void stop();
}
