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

package com.seovic.core.processor;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;
import java.io.IOException;
import java.io.Serializable;


/**
 * An entry processor that puts entry into the cache and optionally returns the
 * old value.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public class Put
        extends AbstractProcessor
        implements Serializable, PortableObject {

    private static final long serialVersionUID = 2438421819803205154L;

    // ---- data members ----------------------------------------------------

    /**
     * Value to put into the cache.
     */
    private Object value;

    /**
     * Flag specifying whether to return the old value.
     */
    private boolean fReturnOld;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public Put() {
    }

    /**
     * Construct Put processor instance.
     *
     * @param value value to put into the cache
     */
    public Put(Object value) {
        this(value, false);
    }

    /**
     * Construct Put processor instance.
     *
     * @param value      value to put into the cache
     * @param fReturnOld flag specifying whether to return the old value
     */
    public Put(Object value, boolean fReturnOld) {
        this.value = value;
        this.fReturnOld = fReturnOld;
    }


    // ---- AbstractProcessor implementation --------------------------------

    /**
     * Process specified entry and return the result.
     *
     * @param entry entry to process
     *
     * @return processing result
     */
    public Object process(InvocableMap.Entry entry) {
        Object oldValue = fReturnOld ? entry.getValue() : null;
        entry.setValue(value, false);
        return oldValue;
    }


    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs during deserialization
     */
    public void readExternal(PofReader reader)
            throws IOException {
        value = reader.readObject(0);
        fReturnOld = reader.readBoolean(1);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeObject(0, value);
        writer.writeBoolean(1, fReturnOld);
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

        Put processor = (Put) o;

        return fReturnOld == processor.fReturnOld
               && (value == null
                   ? processor.value == null
                   : value.equals(processor.value));
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (fReturnOld ? 1 : 0);
        return result;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "Put{" +
               "value=" + value +
               "fReturnOld=" + fReturnOld +
               '}';
    }
}