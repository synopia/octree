package de.funky_clan.coregl;

import org.junit.Test;

import java.util.PriorityQueue;

import static junit.framework.Assert.assertEquals;

/**
 * @author synopia
 */
public class ListTest {
    public static class Entry implements Comparable<Entry> {
        private String name;
        private float priority;

        public Entry(String name, float priority) {
            this.name = name;
            this.priority = priority;
        }

        @Override
        public int compareTo(Entry o) {
            return priority<o.priority ? -1 : (priority>o.priority ? 1 : 0);
        }

        @Override
        public boolean equals(Object obj) {
            return name.equals(obj);
        }
    }

    @Test
    public void testPriorityQueue() {
        PriorityQueue<Entry> queue = new PriorityQueue<Entry>();

        queue.add(new Entry("test", 100));
        queue.add(new Entry("test2", 101));
        queue.add(new Entry("test3", 101));
        queue.add(new Entry("test4", 100));

        assertEquals( "test", queue.poll().name);
        assertEquals( "test4", queue.poll().name);
        assertEquals( "test3", queue.poll().name);
        assertEquals( "test2", queue.poll().name);
    }
}
