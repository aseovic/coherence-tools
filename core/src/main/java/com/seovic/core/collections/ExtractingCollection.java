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


import com.seovic.core.Extractor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author Aleksandar Seovic  2010.11.20
 */
@SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
public class ExtractingCollection<E> implements Collection<E> {
    private Collection   source;
    private Extractor<E> extractor;

    private transient Collection<E> convertedSource;

    public ExtractingCollection(Collection source, Extractor<E> extractor) {
        this.source    = source;
        this.extractor = extractor;
    }

    public int size() {return source.size();}

    public boolean isEmpty() {return source.isEmpty();}

    public boolean contains(Object o) {
        convertSource();
        return convertedSource.contains(o);
    }

    public Iterator<E> iterator() {
        convertSource();
        return convertedSource.iterator();
    }

    public Object[] toArray() {
        return toArray(new Object[source.size()]);
    }

    public <T> T[] toArray(T[] a) {
        convertSource();
        return convertedSource.toArray(a);
    }

    public boolean add(E o) {
        throw new UnsupportedOperationException("This collection is read-only.");
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException("This collection is read-only.");
    }

    public boolean containsAll(Collection<?> c) {
        convertSource();
        return source.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException("This collection is read-only.");
    }

    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This collection is read-only.");
    }

    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This collection is read-only.");
    }

    public void clear() {
        throw new UnsupportedOperationException("This collection is read-only.");
    }

    private void convertSource() {
        if (convertedSource != null) return;

        List<E> converted = new ArrayList<E>(source.size());
        for (Object o : source) {
            converted.add(extractor.extract(o));
        }

        convertedSource = converted;
    }
}
