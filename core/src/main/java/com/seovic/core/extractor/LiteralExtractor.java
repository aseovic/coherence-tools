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
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;


/**
 * Extractor implementation that always returns the literal value it was
 * constructed with.
 *
 * @author Aleksandar Seovic  2012.04.05
 */
public class LiteralExtractor<T>
        implements Extractor<T>, Serializable, PortableObject {

    private static final long serialVersionUID = -5045992499516628565L;

    // ---- data members ----------------------------------------------------

    private T literal;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public LiteralExtractor() {
    }

    /**
     * Construct LiteralExtractor instance.
     *
     * @param literal the literal value to return from {@link #extract(Object)}
     *                method
     */
    public LiteralExtractor(T literal) {
        this.literal = literal;
    }

     // ---- Extractor implementation ----------------------------------------

    /**
     * Return the literal value this instance was constructed with.
     *
     * @param o normally an object to extract value from, but it is ignored by
     *          this particular implementation
     *
     * @return the literal value this instance was constructed with
     */
    @Override
    public T extract(Object o) {
        return literal;
    }

    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs during deserialization
     */
    @SuppressWarnings("unchecked")
    @Override
    public void readExternal(PofReader reader) throws IOException {
        literal = (T) reader.readObject(0);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    @Override
    public void writeExternal(PofWriter writer) throws IOException {
        writer.writeObject(0, literal);
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiteralExtractor extractor = (LiteralExtractor) o;

        return literal == null
                 ? extractor.literal == null
                 : literal.equals(extractor.literal);
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return literal != null ? literal.hashCode() : 0;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "LiteralExtractor{" +
               "literal=" + literal +
               '}';
    }
}
