package de.funky_clan.voxel.data;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenLongObjectHashMap;

import java.util.PriorityQueue;

/**
 * @author synopia
 */
public class FastPriorityQueue<T extends Comparable<T>> {
    private ObjectArrayList queue2 = new ObjectArrayList();
    private boolean dirty = true;
    
    public void add( T object ) {
        if( !queue2.contains(object, false) ) {
            queue2.add(object);
            dirty = true;
        }
    }

    public boolean isEmpty() {
        return queue2.isEmpty();
    }
    
    public T poll() {
        if( dirty ) {
            queue2.sort();
        }
        T e = (T) queue2.get(0);
        queue2.remove(0);
        return e;
    }
    
    public T peek() {
        if( dirty ) {
            queue2.sort();
        }
        return (T) queue2.get(0);
    }
    
    public int size() {
        return queue2.size();
    }
}
