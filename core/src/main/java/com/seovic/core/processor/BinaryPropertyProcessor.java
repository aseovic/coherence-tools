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
import java.io.IOException;


/**
 * Base class for entry processors that need to manipulate properties of binary
 * entries directly.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public abstract class BinaryPropertyProcessor
        extends AbstractBinaryProcessor
        implements PortableObject {
    // ---- data members ----------------------------------------------------

    /**
     * PofNavigator that should be used to navigate to a property.
     */
    protected PofNavigator navigator;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    protected BinaryPropertyProcessor() {
    }

    /**
     * Construct BinaryPropertyProcessor instance.
     *
     * @param navigator PofNavigator to use to find the property
     */
    public BinaryPropertyProcessor(PofNavigator navigator) {
        this.navigator = navigator;
    }

    // ---- helper methods --------------------------------------------------

    /**
     * Get the property value from the specified PofValue object.
     *
     * @param target PofValue to get the property value from
     *
     * @return property value
     */
    protected Object get(PofValue target) {
        return navigator.navigate(target).getValue();
    }

    /**
     * Set the property value into the specified PofValue object.
     *
     * @param target PofValue to set the property value into
     * @param value  new property value
     */
    protected void set(PofValue target, Object value) {
        navigator.navigate(target).setValue(value);
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
        if (o == null || !(o instanceof BinaryPropertyProcessor)) {
            return false;
        }

        BinaryPropertyProcessor processor = (BinaryPropertyProcessor) o;

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
        return "BinaryPropertyProcessor{" +
               "navigator=" + navigator +
               '}';
    }
}