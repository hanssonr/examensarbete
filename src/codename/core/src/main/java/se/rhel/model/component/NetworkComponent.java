package se.rhel.model.component;

/**
 * Created by rkh on 2014-04-25.
 */
public class NetworkComponent implements INetwork, IComponent {

    private int mID;

    public NetworkComponent(int id) {
        mID = id;
    }

    public int getID() {
        return mID;
    }
}
