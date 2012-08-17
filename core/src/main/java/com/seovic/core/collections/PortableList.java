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
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Wrapper list that controls wrapped list's serialization.
 * <p/>
 * This class can be used when you want to store mutable list as the value of a
 * cache entry. It uses provided {@link Factory} to create a list instance
 * during construction and deserialization and delegates all methods defined
 * by the <tt>List</tt> interface to it.
 *
 * @author Aleksandar Seovic  2010.11.08
 */
@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
public class PortableList<E> implements List<E>, PortableObject {
    // ---- data members ----------------------------------------------------

    private Factory<List<E>> factory;
    private List<E> delegate;


    // ---- constructors ----------------------------------------------------

    public PortableList() {
        this(new ArrayListFactory<E>());
    }

    public PortableList(Factory<List<E>> factory) {
        this.factory     = factory;
        this.delegate    = factory.create();
    }


    // ---- List implementation ---------------------------------------------

    public int size() {return delegate.size();}

    public boolean isEmpty() {return delegate.isEmpty();}

    public boolean contains(Object o) {return delegate.contains(o);}

    public Iterator<E> iterator() {return delegate.iterator();}

    public Object[] toArray() {return delegate.toArray();}

    public <T> T[] toArray(T[] ts) {return delegate.toArray(ts);}

    public boolean add(E e) {return delegate.add(e);}

    public boolean remove(Object o) {return delegate.remove(o);}

    public boolean containsAll(Collection<?> objects) {return delegate.containsAll(objects);}

    public boolean addAll(Collection<? extends E> es) {return delegate.addAll(es);}

    public boolean addAll(int i, Collection<? extends E> es) {return delegate.addAll(i, es);}

    public boolean removeAll(Collection<?> objects) {return delegate.removeAll(objects);}

    public boolean retainAll(Collection<?> objects) {return delegate.retainAll(objects);}

    public void clear() {delegate.clear();}

    public E get(int i) {return delegate.get(i);}

    public E set(int i, E e) {return delegate.set(i, e);}

    public void add(int i, E e) {delegate.add(i, e);}

    public E remove(int i) {return delegate.remove(i);}

    public int indexOf(Object o) {return delegate.indexOf(o);}

    public int lastIndexOf(Object o) {return delegate.lastIndexOf(o);}

    public ListIterator<E> listIterator() {return delegate.listIterator();}

    public ListIterator<E> listIterator(int i) {return delegate.listIterator(i);}

    public List<E> subList(int i, int i1) {return delegate.subList(i, i1);}


    // ---- Object methods --------------------------------------------------

    public boolean equals(PortableList other) {
        return factory.equals(other.factory)
               && delegate.equals(other.delegate);    
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PortableList
               ? equals((PortableList) other)
               : delegate.equals(other);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    // ---- helper methods --------------------------------------------------

    public E removeByIndex(int i) {
        return remove(i);
    }


    // ---- PortableObject implementation -----------------------------------

    @Override
    public void readExternal(PofReader reader) throws IOException {
        factory  = (Factory<List<E>>) reader.readObject(0);
        delegate = factory.create();
        reader.readCollection(1, delegate);
    }

    @Override
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeObject(0, factory);
        writer.writeCollection(1, delegate);
    }
}
