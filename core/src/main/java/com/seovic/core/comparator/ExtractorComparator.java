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

package com.seovic.core.comparator;


import com.seovic.core.Extractor;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;


/**
 * Compares objects based on the values of the extracted attributes.
 * <p/>
 * The extracted attributes must be of a primitive type or implement <tt>
 *
 * @author Aleksandar Seovic  2010.09.09
 */
public class ExtractorComparator
        implements Comparator, Serializable, PortableObject {

    private static final long serialVersionUID = -2666490697049717296L;

    private Extractor extractor;

    public ExtractorComparator() {
    }

    public ExtractorComparator(Extractor extractor) {
        this.extractor = extractor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compare(Object o1, Object o2) {
        Comparable a1 = (Comparable) extractor.extract(o1);
        Comparable a2 = (Comparable) extractor.extract(o2);

        if (a1 == null) {
            return a2 == null ? 0 : -1;
        }

        if (a2 == null) {
            return +1;
        }

        return a1.compareTo(a2);
    }

    @Override
    public void readExternal(PofReader reader) throws IOException {
        extractor = (Extractor) reader.readObject(0);
    }

    @Override
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeObject(0, extractor);
    }
}
