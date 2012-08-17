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
import com.seovic.core.factory.ArrayListFactory;
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
import java.util.List;
import java.util.ListIterator;


/**
 * @author Aleksandar Seovic  2010.11.06
 */
@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
public class RemoteList<E> implements List<E> {
    private final NamedCache cache;
    private final Object key;
    private final Factory<List<E>> listFactory;

    public RemoteList(String cacheName, Object key) {
        this(cacheName, key, new ArrayListFactory<E>());
    }

    public RemoteList(String cacheName, Object key, Factory<List<E>> listFactory) {
        this(CacheFactory.getCache(cacheName), key, listFactory);
    }

    public RemoteList(NamedCache cache, Object key) {
        this(cache, key, new ArrayListFactory<E>());
    }

    public RemoteList(NamedCache cache, Object key, Factory<List<E>> listFactory) {
        this.cache = cache;
        this.key   = key;
        this.listFactory = listFactory;
    }


    // ---- List implementation ---------------------------------------------

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
        return listIterator(0);
    }

    @Override
    public Object[] toArray() {
        return remoteList().toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) {
        return remoteList().toArray(ts);
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
    public boolean addAll(int i, Collection<? extends E> objects) {
        return (Boolean) remoteInvoke("addAll", true, i, objects);
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

    @Override
    public E get(int i) {
        return (E) remoteInvoke("get", false, i);
    }

    @Override
    public E set(int i, E e) {
        return (E) remoteInvoke("set", true, i, e);
    }

    @Override
    public void add(int i, E e) {
        remoteInvoke("add", true, i, e);
    }

    @Override
    public E remove(int i) {
        return (E) remoteInvoke("removeByIndex", true, i);
    }

    @Override
    public int indexOf(Object o) {
        return (Integer) remoteInvoke("indexOf", false, o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return (Integer) remoteInvoke("lastIndexOf", false, o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override
    public ListIterator<E> listIterator(int i) {
        return new RemoteListIterator(remoteList(), i);
    }

    @Override
    public List<E> subList(int start, int end) {
        return (List<E>) remoteInvoke("subList", false, start, end);
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
        return remoteList().toString();
    }

    
    // ---- helper methods --------------------------------------------------

    private List<E> remoteList() {
        List<E> result = (List<E>) cache.get(key);
        return result == null ? (List<E>) Collections.emptyList() : result;
    }

    private Object remoteInvoke(String method, boolean mutator, Object... args) {
        return cache.invoke(key, new RemoteListProcessor(listFactory, method, mutator, args));
    }


    // ---- RemoteListIterator class ----------------------------------------

    private class RemoteListIterator implements ListIterator<E> {
        private ListIterator<E> iterator;
        private int cursor = 0;
        private int last = -1;

        public RemoteListIterator(List<E> list, int index) {
            this.iterator = list.listIterator(index);
            this.cursor   = index;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public E next() {
            E next = iterator.next();
            last = cursor++;
            return next;
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public E previous() {
            E previous = iterator.previous();
            last = --cursor;
            return previous;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public void remove() {
            iterator.remove();
            RemoteList.this.remove(last);
            if (last < cursor) {
                cursor--;
            }
        }

        public void set(E e) {
            iterator.set(e);
            RemoteList.this.set(last, e);
        }

        public void add(E e) {
            iterator.add(e);
            RemoteList.this.add(cursor++, e); 
        }
    }


    // ---- RemoteListProcessor class ---------------------------------------

    public static class RemoteListProcessor extends MethodInvocationProcessor {
        private Factory factory;

        public RemoteListProcessor() {
        }

        public RemoteListProcessor(Factory factory, String name, boolean mutator, Object... args) {
            super(name, mutator, args);
            this.factory = factory;
        }

        @Override
        public Object process(InvocableMap.Entry entry) {
            if (entry.getValue() == null) {
                entry.setValue(new PortableList(factory));
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
