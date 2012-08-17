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
 * An entry processor that removes entry from the cache and optionally returns
 * the old value.
 *
 * @author Aleksandar Seovic  2009.06.24
 */
public class Remove
        extends AbstractProcessor
        implements Serializable, PortableObject {

    private static final long serialVersionUID = 6619191473182891582L;

    // ---- data members ----------------------------------------------------

    /**
     * Flag specifying whether to return the old value.
     */
    private boolean fReturnOld;

    // ---- constructors ----------------------------------------------------

    /**
     * Default constructor.
     */
    public Remove() {
    }

    /**
     * Construct Remove processor instance.
     *
     * @param fReturnOld flag specifying whether to return the old value
     */
    public Remove(boolean fReturnOld) {
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
        entry.remove(false);
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
        fReturnOld = reader.readBoolean(0);
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
        writer.writeBoolean(0, fReturnOld);
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

        Remove processor = (Remove) o;
        return fReturnOld == processor.fReturnOld;
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return (fReturnOld ? 1 : 0);
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "Remove{" +
               "fReturnOld=" + fReturnOld +
               '}';
    }
}

