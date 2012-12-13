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

package com.seovic.core.extractor;


import com.seovic.core.Extractor;
import com.seovic.core.objects.DynamicObject;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;

import java.io.IOException;
import java.io.Serializable;


/**
 * Simple implementation of {@link com.seovic.core.Extractor} that extracts value from a {@link DynamicObject}.
 *
 * @author Aleksandar Seovic  2012.12.12
 */
@SuppressWarnings({"unchecked"})
public class DynamicObjectExtractor<T>
        implements Extractor<T>, Serializable, PortableObject {

    private static final long serialVersionUID = -7962853514640000253L;

    // ---- data members ----------------------------------------------------

    /**
     * Property key.
     */
    private String key;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public DynamicObjectExtractor() {
    }

    /**
     * Construct a <tt>MapExtractor</tt> instance.
     *
     * @param key the key to extract value for
     */
    public DynamicObjectExtractor(String key) {
        this.key = key;
    }


    // ---- Extractor implementation ----------------------------------------

    /**
     * {@inheritDoc}
     */
    public T extract(Object target) {
        if (target == null) {
            return null;
        }
        if (!(target instanceof DynamicObject)) {
            throw new IllegalArgumentException(
                    "Extraction target is not a DynamicObject");
        }

        return (T) ((DynamicObject) target).getValue(key);
    }


    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws java.io.IOException if an error occurs during deserialization
     */
    public void readExternal(PofReader reader) throws IOException {
        key = reader.readString(0);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws java.io.IOException if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeString(0, key);
    }


    // ---- Object methods --------------------------------------------------

    /**
     * Test objects for equality.
     *
     * @param o object to compare this object with
     *
     * @return <tt>true</tt> if the specified object is equal to this object
     *         <tt>false</tt> otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DynamicObjectExtractor extractor = (DynamicObjectExtractor) o;
        return key.equals(extractor.key);
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "DynamicObjectExtractor{" +
               "key='" + key + '\'' +
               '}';
    }
}
