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
import com.tangosol.util.BinaryEntry;
import java.io.IOException;


/**
 * An entry processor that puts entry into the cache and optionally returns the
 * old value.
 * <p/>
 * This processor has identical behavior to {@link Put}, but it avoids
 * unnecessary serialization within the cluster by updating BinaryEntry
 * directly.
 * <p/>
 * This implies that it can only be used with the partitioned caches.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public class BinaryPut
        extends AbstractBinaryProcessor
        implements PortableObject {
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
    public BinaryPut() {
    }

    /**
     * Construct BinaryPut instance.
     *
     * @param value value to put into the cache
     */
    public BinaryPut(Object value) {
        this(value, false);
    }

    /**
     * Construct BinaryPut instance.
     *
     * @param value      value to put into the cache
     * @param fReturnOld flag specifying whether to return the old value
     */
    public BinaryPut(Object value, boolean fReturnOld) {
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
    public Object process(BinaryEntry entry) {
        Object oldValue = fReturnOld ? entry.getValue() : null;
        entry.updateBinaryValue(getBinaryValue("value"));
        return oldValue;
    }


    // ---- accessors -------------------------------------------------------

    /**
     * Return deserialized value.
     *
     * @return deserialized value
     */
    protected Object getValue() {
        Object value = this.value;
        if (value == null) {
            this.value = value = fromBinary("value");
        }
        return value;
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
        super.readExternal(reader);
        setBinaryValue("value", reader.readBinary(0));
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
        super.writeExternal(writer);
        writer.writeBinary(0, toBinary(value));
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

        BinaryPut processor = (BinaryPut) o;

        return super.equals(o)
               && fReturnOld == processor.fReturnOld
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
        int result = super.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
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
        return "BinaryPut{" +
               "value=" + getValue() +
               "fReturnOld=" + fReturnOld +
               '}';
    }
}