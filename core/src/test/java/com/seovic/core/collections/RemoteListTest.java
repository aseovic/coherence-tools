/*
 * Copyright 2009 Aleksandar Seovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seovic.core.collections;


import com.seovic.core.factory.LinkedListFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2010.11.08
 */
public class RemoteListTest {
    private static final NamedCache cache = CacheFactory.getCache("test-lists");

    @Before
    public void setup() {
        cache.remove(1L);
    }

    @Test
    public void testCreation() {
        RemoteList l = new RemoteList(cache, 1L);
        assertEquals(0, l.size());
        assertTrue(l.isEmpty());
    }

    @Test
    public void testAdditionAndRemoval() {
        RemoteList<String> l = new RemoteList<String>(cache, 1L);
        assertEquals(0, l.size());

        l.add("one");
        l.add("two");
        l.add("three");
        assertEquals(3, l.size());
        assertFalse(l.isEmpty());
        assertTrue(l.contains("one"));
        
        l.remove("one");
        assertEquals(2, l.size());
        assertFalse(l.contains("one"));

        l.remove(1);
        assertEquals(1, l.size());
        assertEquals("two", l.get(0));

        l.add(0, "one");
        assertEquals(2, l.size());
        assertEquals("two", l.get(1));

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testAdditionAndRemovalWithPrimitives() {
        RemoteList<Long> l = new RemoteList<Long>("test-lists", 1L);
        assertEquals(0, l.size());

        l.add(0L);
        l.add(10L);
        l.add(200L);
        l.add(3000L);
        assertEquals(4, l.size());
        assertFalse(l.isEmpty());
        assertTrue(l.contains(10L));

        l.remove(10L);
        assertEquals(3, l.size());
        assertFalse(l.contains(10L));

        l.remove(0);
        l.remove(1);
        assertEquals(1, l.size());
        assertEquals(200L, (long) l.get(0));

        l.add(0, 10L);
        assertEquals(2, l.size());
        assertEquals(200L, (long) l.get(1));

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testBulkAdditionAndRemoval() {
        RemoteList<String> l = new RemoteList<String>(cache, 1L, new LinkedListFactory<String>());
        assertEquals(0, l.size());

        l.addAll(Arrays.asList("one", "two", "three", "four", "five"));
        assertEquals(5, l.size());
        assertFalse(l.isEmpty());
        assertTrue(l.containsAll(Arrays.asList("one", "three", "five")));

        l.retainAll(Arrays.asList("one", "two", "three"));
        assertEquals(3, l.size());
        assertFalse(l.containsAll(Arrays.asList("one", "three", "five")));

        l.removeAll(Arrays.asList("one", "three"));
        assertEquals(1, l.size());
        assertEquals("two", l.get(0));

        l.addAll(0, Arrays.asList("zero", "one"));
        assertEquals(3, l.size());
        assertEquals("two", l.get(2));

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testBulkAdditionAndRemovalWithPrimitives() {
        RemoteList<Integer> l = new RemoteList<Integer>("test-lists", 1L, new LinkedListFactory<Integer>());
        assertEquals(0, l.size());

        l.addAll(Arrays.asList(0, 1, 2, 3, 4, 5));
        assertEquals(6, l.size());
        assertFalse(l.isEmpty());
        assertTrue(l.containsAll(Arrays.asList(1, 3, 5)));

        l.retainAll(Arrays.asList(1, 2, 3));
        assertEquals(3, l.size());
        assertFalse(l.containsAll(Arrays.asList(1, 3, 5)));

        l.removeAll(Arrays.asList(1, 3));
        assertEquals(1, l.size());
        assertEquals(2, (int) l.get(0));

        l.addAll(0, Arrays.asList(0, 1));
        assertEquals(3, l.size());
        assertEquals(2, (int) l.get(2));

        l.clear();
        assertTrue(l.isEmpty());
    }
    
    @Test
    public void testModification() {
        RemoteList<Integer> l = new RemoteList<Integer>("test-lists", 1L, new LinkedListFactory<Integer>());

        l.addAll(Arrays.asList(0, 1, 2, 3));
        for (int i = 0; i < 4; i++) {
            l.set(i, l.get(i) * 10);
        }

        assertEquals(Arrays.asList(0, 10, 20, 30), l);
        assertTrue(l.equals(Arrays.asList(0, 10, 20, 30)));
    }

    @Test
    public void testSearch() {
        RemoteList<Integer> l = new RemoteList<Integer>(cache, 1L);

        l.addAll(Arrays.asList(1, 2, 3, 1, 2, 3, 1, 2, 3));
        assertEquals(1, l.indexOf(2));
        assertEquals(6, l.lastIndexOf(1));
    }

    @Test
    public void testSublist() {
        RemoteList<Integer> l = new RemoteList<Integer>(cache, 1L);

        l.addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        assertEquals(Arrays.asList(2, 3, 4, 5), l.subList(2, 6));
        assertEquals(Arrays.asList(8, 9, 10), l.subList(8, 11));
    }

    @Test
    public void testToArray() {
        RemoteList<Integer> l = new RemoteList<Integer>(cache, 1L);

        l.addAll(Arrays.asList(0, 1, 2));
        assertTrue(Arrays.equals(new Object[] {0, 1, 2}, l.toArray()));
        assertTrue(Arrays.equals(new Integer[] {0, 1, 2}, l.toArray(new Integer[3])));
    }

    @Test
    public void testIterator() {
        RemoteList<String> l = new RemoteList<String>(cache, 1L, new LinkedListFactory<String>());

        l.addAll(Arrays.asList("one", "two", "three"));
        for (Iterator<String> it = l.iterator(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
        assertTrue(l.isEmpty());

        l.addAll(Arrays.asList("one", "two", "three"));
        for (ListIterator<String> it = l.listIterator(2); it.hasPrevious(); ) {
            it.previous();
            it.remove();
        }
        assertEquals(1, l.size());
        assertEquals("three", l.get(0));

        l.clear();
        l.addAll(Arrays.asList("one", "two", "three"));
        for (ListIterator<String> it = l.listIterator(); it.hasNext(); ) {
            String str = it.next();
            it.set(str.toUpperCase());
        }
        assertEquals(Arrays.asList("ONE", "TWO", "THREE"), l);

        ListIterator<String> it = l.listIterator();
        it.add("0");
        it.next();
        it.next();
        it.add("2 1/2");
        it.next();
        it.add("4");
        assertEquals(Arrays.asList("0", "ONE", "TWO", "2 1/2", "THREE", "4"), l);


    }
}
