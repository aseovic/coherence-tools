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


import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.PofValueParser;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Abstract base class for entry processors that need to perform direct binary
 * manipulation of cached objects using PofValue.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
@SuppressWarnings({"unchecked"})
public abstract class AbstractBinaryProcessor
        extends AbstractProcessor
        implements PortableObject {
    // ---- data members ----------------------------------------------------

    /**
     * PofContext to use.
     */
    private transient PofContext pofContext;

    /**
     * Storage for named binary values.
     */
    private transient Map<String, Binary> binValues = new HashMap();

    // ----- abstract methods -----------------------------------------------

    /**
     * Process a single binary entry.
     *
     * @param entry entry to process
     *
     * @return processing result
     */
    protected abstract Object process(BinaryEntry entry);


    // ----- AbstractProcessor implementation -------------------------------

    /**
     * {@inheritDoc}
     */
    public Object process(InvocableMap.Entry entry) {
        return process((BinaryEntry) entry);
    }


    // ---- helper methods --------------------------------------------------

    /**
     * Return PofContext to use.
     *
     * @return PofContext to use
     */
    public PofContext getPofContext() {
        return pofContext;
    }

    /**
     * Return binary value with the specified name.
     *
     * @param name name of the binary value
     *
     * @return binary value with the specified name
     */
    protected Binary getBinaryValue(String name) {
        return binValues.get(name);
    }

    /**
     * Associate binary value with the specified name.
     *
     * @param name  name to associate value with
     * @param value binary value
     */
    protected void setBinaryValue(String name, Binary value) {
        binValues.put(name, value);
    }

    /**
     * Return PofValue for the specified binary value.
     *
     * @param binValue binary value to parse
     *
     * @return parsed PofValue
     */
    protected PofValue getPofValue(Binary binValue) {
        return PofValueParser.parse(binValue, pofContext);
    }

    /**
     * Return PofValue for the specified binary value's name.
     *
     * @param name name of the binary value to parse
     *
     * @return parsed PofValue
     *
     * @see #setBinaryValue(String, Binary)
     */
    protected PofValue getPofValue(String name) {
        return getPofValue(getBinaryValue(name));
    }

    /**
     * Return deserialized object for the specified binary value.
     *
     * @param binValue binary value to deserialize
     *
     * @return deserialized object
     */
    protected Object fromBinary(Binary binValue) {
        return ExternalizableHelper.fromBinary(binValue, pofContext);
    }

    /**
     * Return deserialized object for the specified binary value's name.
     *
     * @param name name of the binary value to parse
     *
     * @return deserialized object
     *
     * @see #setBinaryValue(String, Binary)
     */
    protected Object fromBinary(String name) {
        return fromBinary(getBinaryValue(name));
    }

    /**
     * Serialize specified object into binary value.
     *
     * @param o object to serialize
     *
     * @return serialized binary value
     */
    protected Binary toBinary(Object o) {
        return ExternalizableHelper.toBinary(o, pofContext);
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
        pofContext = reader.getPofContext();
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
        pofContext = writer.getPofContext();
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
        if (o == null || !(o instanceof AbstractBinaryProcessor)) {
            return false;
        }

        AbstractBinaryProcessor processor = (AbstractBinaryProcessor) o;

        return (binValues == null
                ? processor.binValues == null
                : binValues.equals(processor.binValues));
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return binValues != null ? binValues.hashCode() : 0;
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "AbstractBinaryProcessor{" +
               "pofContext=" + pofContext +
               "binValues=" + binValues +
               '}';
    }
}
