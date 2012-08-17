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
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 * Wrapper set that controls wrapped set's serialization.
 * <p/>
 * This class can be used when you want to store mutable set as the value of a
 * cache entry. It uses provided {@link Factory} to create a set instance
 * during construction and deserialization and delegates all methods defined
 * by the <tt>Set</tt> interface to it.
 *
 * @author Aleksandar Seovic  2010.11.08
 */
@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
public class PortableSet<E> implements Set<E>, PortableObject {
    // ---- data members ----------------------------------------------------

    private Factory<Set<E>> factory;
    private Set<E> delegate;


    // ---- constructors ----------------------------------------------------

    public PortableSet() {
        this(new HashSetFactory<E>());
    }

    public PortableSet(Factory<Set<E>> factory) {
        this.factory     = factory;
        this.delegate    = factory.create();
    }


    // ---- Set implementation ----------------------------------------------

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

    public boolean retainAll(Collection<?> objects) {return delegate.retainAll(objects);}

    public boolean removeAll(Collection<?> objects) {return delegate.removeAll(objects);}

    public void clear() {delegate.clear();}


    // ---- Object methods --------------------------------------------------

    public boolean equals(PortableSet other) {
        return factory.equals(other.factory)
               && delegate.equals(other.delegate);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PortableSet
               ? equals((PortableSet) other)
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


    // ---- PortableObject implementation -----------------------------------

    @Override
    public void readExternal(PofReader reader) throws IOException {
        factory  = (Factory<Set<E>>) reader.readObject(0);
        delegate = factory.create();
        reader.readCollection(1, delegate);
    }

    @Override
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeObject(0, factory);
        writer.writeCollection(1, delegate);
    }
}