package moonlightflower.com.superioribus2.shared;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

public class ListenerSet<T> {
    private Set<T> _set = new HashSet<>();//Collections.newSetFromMap(new WeakHashMap<T, Boolean>());

    public int size() {
        return _set.size();
    }

    public void clear() {
        _set.clear();
    }

    public void add(T val) {
        _set.add(val);
    }

    public void remove(T val) {
        _set.remove(val);
    }

    public void fire(ListenerAction<T> action) {
        Set<T> set = new HashSet<>(_set);

        for (T el : set) {
            action.fire(el);
        }
    }

    public ListenerSet() {

    }
}