package se.rhel.utils;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

/**
 * Created by rkh on 2014-05-02.
 */
public class CombiMap<T> {

    private int mPos = 0;
    private HashMap<Integer, T> mList;

    public CombiMap() {
        mList = new HashMap<>();
    }

    public void add(T toAdd) {
        mList.put(mPos++, toAdd);
    }

    public void set(int index, T toAdd) {
        mList.put(index, toAdd);
    }

    public T get(int index) {
        return mList.get(index);
    }

    public Array<T> toArray() {
        Array<T> ret = new Array<>();
        for(T t : mList.values()) {
            ret.add(t);
        }

        return ret;
    }

}
