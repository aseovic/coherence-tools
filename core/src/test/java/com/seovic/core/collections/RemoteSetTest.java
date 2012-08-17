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


import com.seovic.core.factory.TreeSetFactory;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2010.11.08
 */
public class RemoteSetTest {
    private static final NamedCache cache = CacheFactory.getCache("test-sets");

    @Before
    public void setup() {
        cache.remove(1L);
    }

    @Test
    public void testCreation() {
        RemoteSet l = new RemoteSet(cache, 1L);
        assertEquals(0, l.size());
        assertTrue(l.isEmpty());
    }

    @Test
    public void testAdditionAndRemoval() {
        RemoteSet<String> l = new RemoteSet<String>(cache, 1L);
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

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testAdditionAndRemovalWithPrimitives() {
        RemoteSet<Long> l = new RemoteSet<Long>("test-sets", 1L);
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

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testBulkAdditionAndRemoval() {
        RemoteSet<String> l = new RemoteSet<String>(cache, 1L);
        assertEquals(0, l.size());

        l.addAll(Arrays.asList("one", "two", "two", "three", "three", "four", "five"));
        assertEquals(5, l.size());
        assertFalse(l.isEmpty());
        assertTrue(l.containsAll(Arrays.asList("one", "three", "five")));

        l.retainAll(Arrays.asList("one", "two", "three"));
        assertEquals(3, l.size());
        assertFalse(l.containsAll(Arrays.asList("one", "three", "five")));

        l.removeAll(Arrays.asList("one", "three"));
        assertEquals(1, l.size());
        assertTrue(l.contains("two"));

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testBulkAdditionAndRemovalWithPrimitives() {
        RemoteSet<Integer> l = new RemoteSet<Integer>("test-sets", 1L);
        assertEquals(0, l.size());

        l.addAll(Arrays.asList(0, 1, 1, 2, 2, 3, 3, 4, 5));
        assertEquals(6, l.size());
        assertFalse(l.isEmpty());
        assertTrue(l.containsAll(Arrays.asList(1, 3, 5)));

        l.retainAll(Arrays.asList(1, 2, 3));
        assertEquals(3, l.size());
        assertFalse(l.containsAll(Arrays.asList(1, 3, 5)));

        l.removeAll(Arrays.asList(1, 3));
        assertEquals(1, l.size());
        assertTrue(l.contains(2));

        l.clear();
        assertTrue(l.isEmpty());
    }

    @Test
    public void testToArray() {
        RemoteSet<Integer> l = new RemoteSet<Integer>(cache, 1L, new TreeSetFactory<Integer>());

        l.addAll(Arrays.asList(0, 1, 2));
        assertTrue(Arrays.equals(new Object[] {0, 1, 2}, l.toArray()));
        assertTrue(Arrays.equals(new Integer[] {0, 1, 2}, l.toArray(new Integer[3])));
    }

    @Test
    public void testIterator() {
        RemoteSet<String> l = new RemoteSet<String>(cache, 1L);

        l.addAll(Arrays.asList("one", "two", "three"));
        for (Iterator<String> it = l.iterator(); it.hasNext(); ) {
            it.next();
            it.remove();
        }
        assertTrue(l.isEmpty());
    }
}