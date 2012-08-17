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
import com.tangosol.io.pof.reflect.PofNavigator;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.LiteMap;

import java.io.IOException;
import java.util.Map;
import java.util.Set;


/**
 * VersionedPut is an EntryProcessor that assumes that entry values have an
 * integer member representing object's version, and updates entry's value if
 * and only if the version of the specified value matches to the version of the
 * current value. In case of the match, the BinaryVersionedPut will increment
 * the version indicator before the value is updated.
 * <p/>
 * This processor is functionally equivalent to the built-in VersionedPut
 * processor, but it avoids unnecessary serialization within the cluster by
 * manipulating binary representations of old and new value directly.
 * <p/>
 * The only minor difference is that this processor does not require you to
 * implement Versionable interface, but it requires that the version property is
 * represented by an integer value.
 * <p/>
 * The fact that binary values are directly manipulated implies that this entry
 * processor can only be used with the partitioned caches.
 *
 * @author Aleksandar Seovic  2009.09.30
 */
public class BinaryVersionedPut
        extends BinaryPropertyProcessor
        implements PortableObject {
    // ---- data members ----------------------------------------------------

    /**
     * Used internally to deifferentiate between "no result" and "null result".
     */
    private static final Object NO_RESULT = new Object();

    /**
     * Value to update entry with.
     */
    private Object value;

    /**
     * Flag specifying whether the value should be inserted if it is not already
     * present in the cache.
     */
    private boolean fAllowInsert;

    /**
     * Flag specifying whether the current value should be returned if the
     * versions do not match
     */
    private boolean fReturn;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public BinaryVersionedPut() {
    }

    /**
     * Construct a BinaryVersionedPut that updates an entry with a new value if
     * and only if the version of the new value matches to the version of the
     * current entry's value (which must exist).
     * <p/>
     * The result of the {@link #process(BinaryEntry)} invocation does not
     * return any result.
     *
     * @param value        a value to update an entry with
     * @param versionIndex an index of the version property within the value
     */
    public BinaryVersionedPut(Object value, int versionIndex) {
        this(value, new SimplePofPath(versionIndex), false, false);
    }

    /**
     * Construct a BinaryVersionedPut that updates an entry with a new value if
     * and only if the version of the new value matches to the version of the
     * current entry's value.
     * <p/>
     * This processor optionally returns the current value as a result of the
     * invocation if it has not been updated (the versions did not match).
     *
     * @param value        a value to update an entry with
     * @param versionIndex an index of the version property within the value
     * @param fAllowInsert specifies whether or not an insert should be allowed
     *                     (no currently existing value)
     * @param fReturn      specifies whether or not the processor should return
     *                     the current value in case it has not been updated
     */
    public BinaryVersionedPut(Object value, int versionIndex,
                              boolean fAllowInsert, boolean fReturn) {
        this(value, new SimplePofPath(versionIndex), fAllowInsert, fReturn);
    }

    /**
     * Construct BinaryVersionedPut instance.
     *
     * @param value        a value to update an entry with
     * @param navigator    an index of the version property within the value
     * @param fAllowInsert specifies whether or not an insert should be allowed
     *                     (no currently existing value)
     * @param fReturn      specifies whether or not the processor should return
     *                     the current value in case it has not been updated
     */
    public BinaryVersionedPut(Object value, PofNavigator navigator,
                              boolean fAllowInsert, boolean fReturn) {
        super(navigator);
        this.value = value;
        this.fAllowInsert = fAllowInsert;
        this.fReturn = fReturn;
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
        Object result = processEntry(entry);
        return result == NO_RESULT ? null : result;
    }

    /**
     * Process all entries in the specified set.
     *
     * @param setEntries set of entries to process
     *
     * @return map of processed entries' keys to processing result
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public Map processAll(Set setEntries) {
        Map results = new LiteMap();

        for (BinaryEntry entry : (Set<BinaryEntry>) setEntries) {
            Object result = processEntry(entry);
            if (result != NO_RESULT) {
                results.put(entry.getKey(), result);
            }
        }

        return results;
    }

    // ---- helper methods --------------------------------------------------

    /**
     * Process the specified entry.
     *
     * @param entry entry to process
     *
     * @return the result of processing the entry; can be NO_RESULT
     */
    protected Object processEntry(BinaryEntry entry) {
        Binary binCurrent = entry.getBinaryValue();
        if (binCurrent == null && !fAllowInsert) {
            return fReturn ? null : NO_RESULT;
        }

        PofValue pvCurrent = getPofValue(binCurrent);
        PofValue pvNew = getPofValue("newValue");

        Integer versionCurrent = (Integer) get(pvCurrent);
        Integer versionNew = (Integer) get(pvNew);

        if (versionCurrent.equals(versionNew)) {
            set(pvNew, versionNew + 1);
            entry.updateBinaryValue(pvNew.applyChanges());
            return NO_RESULT;
        }

        return fReturn ? pvCurrent.getValue() : NO_RESULT;
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
        setBinaryValue("newValue", reader.readBinary(10));
        fAllowInsert = reader.readBoolean(11);
        fReturn = reader.readBoolean(12);
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
        writer.writeBinary(10, toBinary(value));
        writer.writeBoolean(11, fAllowInsert);
        writer.writeBoolean(12, fReturn);
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

        BinaryVersionedPut put = (BinaryVersionedPut) o;
        return super.equals(o)
               && fAllowInsert == put.fAllowInsert
               && fReturn == put.fReturn
               && (value == null
                   ? put.value == null
                   : value.equals(put.value));
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
        result = 31 * result + (fAllowInsert ? 1 : 0);
        result = 31 * result + (fReturn ? 1 : 0);
        return result;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "BinaryVersionedPut{" +
               "navigator=" + navigator +
               ", value=" + value +
               ", allowInsert=" + fAllowInsert +
               ", return=" + fReturn +
               '}';
    }
}