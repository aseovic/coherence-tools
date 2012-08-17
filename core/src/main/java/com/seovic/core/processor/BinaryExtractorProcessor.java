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
import com.tangosol.io.pof.reflect.AbstractPofValue;
import com.tangosol.io.pof.reflect.PofNavigator;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.PofValueParser;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;

import java.io.IOException;


/**
 * An entry processor that extracts Binary value from a cache entry.
 * <p/>
 * Unlike built-in ExtractorProcessor, this processor returns extracted object
 * in a serialized binary form, completely avoiding cluster-side
 * deserialization.
 * <p/>
 * This processor can only be used with the partitioned caches.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public class BinaryExtractorProcessor
        extends AbstractBinaryProcessor
        implements PortableObject {
    // ---- data members ----------------------------------------------------

    /**
     * PofNavigator that should be used to extract child object.
     */
    private PofNavigator navigator;

    // ---- constructors ----------------------------------------------------

    /**
     * Default constructor.
     */
    public BinaryExtractorProcessor() {
    }

    /**
     * Construct BinaryExtractorProcessor instance.
     *
     * @param indices an array of child property indices that should be used to
     *                navigate POF value
     */
    public BinaryExtractorProcessor(int... indices) {
        navigator = new SimplePofPath(indices);
    }

    /**
     * Construct BinaryExtractorProcessor instance.
     *
     * @param navigator PofNavigator to use to extract child property. If
     *                  <tt>null</tt>, binary value of the entry itself will be
     *                  returned.
     */
    public BinaryExtractorProcessor(PofNavigator navigator) {
        this.navigator = navigator;
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
        if (binValue == null || navigator == null) {
            return binValue;
        }

        PofValue root = PofValueParser.parse(binValue, getPofContext());
        PofValue child = navigator.navigate(root);
        return ((AbstractPofValue) child).getSerializedValue().toBinary();
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
        navigator = (PofNavigator) reader.readObject(0);
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
        writer.writeObject(0, navigator);
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

        BinaryExtractorProcessor processor = (BinaryExtractorProcessor) o;

        return super.equals(o)
               && (navigator == null
                   ? processor.navigator == null
                   : navigator.equals(processor.navigator));
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (navigator != null
                                ? navigator.hashCode()
                                : 0);
        return result;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "BinaryExtractorProcessor{" +
               "navigator=" + navigator +
               '}';
    }
}