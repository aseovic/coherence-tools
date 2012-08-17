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


import com.seovic.core.util.Numbers;

import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.reflect.PofNavigator;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;

import java.io.IOException;


/**
 * An entry processor that can be used to increment numeric property.
 * <p/>
 * This processor has identical behavior to the built-in NumberIncrementor, but
 * it avoids unnecessary serialization within the cluster by updating
 * BinaryEntry directly.
 * <p/>
 * This implies that it can only be used with the partitioned caches.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public class BinaryNumberIncrementor
        extends BinaryPropertyProcessor
        implements PortableObject {
    // ---- data members ----------------------------------------------------

    /**
     * Number that the property should be incremented by.
     */
    private Number numInc;

    /**
     * Flag specifying whether to return pre-increment or post-increment value.
     */
    private boolean fPostInc;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public BinaryNumberIncrementor() {
    }

    /**
     * Construct BinaryNumberIncrementor instance.
     *
     * @param numInc   the Number representing the magnitude and sign of the
     *                 increment
     * @param fPostInc pass true to return the value as it was before it was
     *                 incremented, or pass false to return the value as it is
     *                 after it is incremented
     * @param indices  an array of child property indices that should be used to
     *                 navigate POF value
     */
    public BinaryNumberIncrementor(Number numInc, boolean fPostInc,
                                   int... indices) {
        this(numInc, fPostInc, new SimplePofPath(indices));
    }

    /**
     * Construct BinaryNumberIncrementor instance.
     *
     * @param numInc    the Number representing the magnitude and sign of the
     *                  increment
     * @param fPostInc  pass true to return the value as it was before it was
     *                  incremented, or pass false to return the value as it is
     *                  after it is incremented
     * @param navigator PofNavigator to use to extract chile value. If
     *                  <tt>null</tt>, binary value of an entry will be
     *                  returned.
     */
    public BinaryNumberIncrementor(Number numInc, boolean fPostInc,
                                   PofNavigator navigator) {
        super(navigator);
        this.numInc = numInc;
        this.fPostInc = fPostInc;
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
        Binary binValue = entry.getBinaryValue();
        if (binValue == null) {
            return binValue;
        }

        PofValue pofValue = getPofValue(binValue);
        Number oldValue = (Number) get(pofValue);
        if (oldValue == null) {
            oldValue = Numbers.getDefaultValue(numInc.getClass());
        }

        Number newValue = Numbers.add(oldValue, numInc);
        set(pofValue, newValue);
        entry.updateBinaryValue(pofValue.applyChanges());

        return fPostInc ? newValue : oldValue;
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
        numInc = (Number) reader.readObject(10);
        fPostInc = reader.readBoolean(11);
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
        writer.writeObject(10, numInc);
        writer.writeBoolean(11, fPostInc);
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

        BinaryNumberIncrementor processor = (BinaryNumberIncrementor) o;

        return super.equals(o)
               && numInc.equals(processor.numInc)
               && fPostInc == processor.fPostInc;
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + numInc.hashCode();
        result = 31 * result + (fPostInc ? 1 : 0);
        return result;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "BinaryNumberIncrementor{" +
               "navigator=" + navigator +
               ", numInc=" + numInc +
               ", fPostInc=" + fPostInc +
               '}';
    }
}