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


import com.seovic.core.Factory;
import com.seovic.core.factory.HashSetFactory;
import com.seovic.core.processor.MethodInvocationProcessor;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.InvocableMap;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Aleksandar Seovic  2010.11.06
 */
@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
public class RemoteSet<E> implements Set<E> {
    private final NamedCache cache;
    private final Object key;
    private final Factory<Set<E>> factory;

    public RemoteSet(String cacheName, Object key) {
        this(cacheName, key, new HashSetFactory<E>());
    }

    public RemoteSet(String cacheName, Object key, Factory<Set<E>> factory) {
        this(CacheFactory.getCache(cacheName), key, factory);
    }

    public RemoteSet(NamedCache cache, Object key) {
        this(cache, key, new HashSetFactory<E>());
    }

    public RemoteSet(NamedCache cache, Object key, Factory<Set<E>> factory) {
        this.cache = cache;
        this.key   = key;
        this.factory = factory;
    }


    // ---- Set implementation ---------------------------------------------

    @Override
    public int size() {
        return (Integer) remoteInvoke("size", false);
    }

    @Override
    public boolean isEmpty() {
        return (Boolean) remoteInvoke("isEmpty", false);
    }

    @Override
    public boolean contains(Object o) {
        return (Boolean) remoteInvoke("contains", false, o);
    }

    @Override
    public Iterator<E> iterator() {
        return new RemoteSetIterator(remoteSet());
    }

    @Override
    public Object[] toArray() {
        return remoteSet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return remoteSet().toArray(ts);
    }

    @Override
    public boolean add(E e) {
        return (Boolean) remoteInvoke("add", true, e);
    }

    @Override
    public boolean remove(Object o) {
        return (Boolean) remoteInvoke("remove", true, o);
    }

    @Override
    public boolean containsAll(Collection<?> objects) {
        return (Boolean) remoteInvoke("containsAll", false, objects);
    }

    @Override
    public boolean addAll(Collection<? extends E> objects) {
        return (Boolean) remoteInvoke("addAll", true, objects);
    }

    @Override
    public boolean removeAll(Collection<?> objects) {
        return (Boolean) remoteInvoke("removeAll", true, objects);
    }

    @Override
    public boolean retainAll(Collection<?> objects) {
        return (Boolean) remoteInvoke("retainAll", true, objects);
    }

    @Override
    public void clear() {
        remoteInvoke("clear", true);
    }


    // ---- Object methods --------------------------------------------------

    @Override
    public boolean equals(Object o) {
        return (Boolean) remoteInvoke("equals", false, o);
    }

    @Override
    public int hashCode() {
        return (Integer) remoteInvoke("hashCode", false);
    }

    @Override
    public String toString() {
        return remoteSet().toString();
    }


    // ---- helper methods --------------------------------------------------

    private Set<E> remoteSet() {
        Set<E> result = (Set<E>) cache.get(key);
        return result == null ? (Set<E>) Collections.emptySet() : result;
    }

    private Object remoteInvoke(String method, boolean mutator, Object... args) {
        return cache.invoke(key, new RemoteSetProcessor(factory, method, mutator, args));
    }


    // ---- RemoteSetIterator class -----------------------------------------

    private class RemoteSetIterator implements Iterator<E> {
        private Iterator<E> iterator;
        private E last;

        public RemoteSetIterator(Set<E> set) {
            this.iterator = set.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public E next() {
            last = iterator.next();
            return last;
        }

        public void remove() {
            iterator.remove();
            RemoteSet.this.remove(last);
        }
    }


    // ---- RemoteSetProcessor class ----------------------------------------

    public static class RemoteSetProcessor extends MethodInvocationProcessor {
        private Factory factory;

        public RemoteSetProcessor() {
        }

        public RemoteSetProcessor(Factory factory, String name, boolean mutator, Object... args) {
            super(name, mutator, args);
            this.factory = factory;
        }

        @Override
        public Object process(InvocableMap.Entry entry) {
            if (entry.getValue() == null) {
                entry.setValue(new PortableSet(factory));
            }
            return super.process(entry);
        }

        @Override
        public void readExternal(PofReader reader) throws IOException {
            super.readExternal(reader);
            factory = (Factory) reader.readObject(10);
        }

        @Override
        public void writeExternal(PofWriter writer) throws IOException {
            super.writeExternal(writer);
            writer.writeObject(10, factory);
        }
    }
}