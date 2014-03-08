package se.rhel.util;

/**
 * Created by Emil on 2014-03-04.
 * assigned to libgdx-gradle-template in se.rhel
 */
public class Utils {

    private static Utils INSTANCE = null;

    // Private constructor
    private Utils() {}

    public static synchronized Utils getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Utils();
        }
        return INSTANCE;
    }

    // Utils functions
    private static int UNIQUE_ID = 1;

    /**
     * Generate a thread safe (hopefully) unique ID
     * @return
     */
    public synchronized int generateUniqueId() {
        return UNIQUE_ID++;
    }

}
