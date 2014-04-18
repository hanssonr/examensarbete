package se.rhel.screen;

import se.rhel.CodeName;
import se.rhel.event.ModelListener;
import se.rhel.event.ViewListener;

/**
 * Group: Logic
 * Created by Emil on 2014-03-30.
 */
public abstract class Controller extends AbstactController implements ViewListener, ModelListener {

    public Controller(CodeName game) {
        super(game);
    }

}
