package se.rhel;

/**
 * Created by Emil on 2014-02-27.
 * assigned to libgdx-gradle-template in se.rhel
 */
public interface EndPoint extends Runnable {

    /* Should run this endpoint until stop is called */
    @Override
    public void run();

    /* Starts a new thread that calls run */
    public void start();

    /* Closes this end point and causes run to return */
    public void stop();


}
