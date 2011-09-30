package de.funky_clan.voxel.data;

import cern.colt.map.OpenLongObjectHashMap;

import java.util.PriorityQueue;

/**
 * @author synopia
 */
public class FastPriorityQueue<T> {
    private static class Entry<T> implements Comparable<Entry> {
        private long key;
        private T object;
        private float priority;

        private Entry(long key, T object, float priority) {
            this.key = key;
            this.object = object;
            this.priority = priority;
        }

        @Override
        public int compareTo(Entry o) {
            return priority<o.priority ? -1 : (priority>o.priority ? 1 : 0);
        }
    }
    
    private PriorityQueue<Entry<T>> queue = new PriorityQueue<Entry<T>>();
    private OpenLongObjectHashMap entries = new OpenLongObjectHashMap();

    public void add( long key, T object, float priority ) {
        if( !entries.containsKey(key) ) {
            Entry<T> e = new Entry<T>(key, object, priority);
            entries.put(key, e);
            queue.add(e);
        } else {
            Entry<T> e = (Entry<T>) entries.get(key);
            e.priority = priority;
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    public T poll() {
        Entry<T> e = queue.poll();
        entries.removeKey(e.key);
        return e.object;
    }
    
    public T peek() {
        return queue.peek().object;
    }
    
    public int size() {
        return entries.size();
    }
}
