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

package com.seovic.core.factory;


import com.seovic.core.Factory;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import java.io.IOException;


/**
 * {@link Factory} implementation that uses reflection to create an instance of
 * a specified class.
 * <p/>
 * Note that the specified class must have public default constructor in order
 * for this factory to work.
 *
 * @author Aleksandar Seovic  2010.11.08
 */
public class ReflectionFactory<T>
        extends AbstractFactory<T> {

    private static final long serialVersionUID = -6500905712665625713L;

    // ---- data members ----------------------------------------------------

    private String className;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public ReflectionFactory() {
    }

    /**
     * Construct a <tt>ReflectionFactory</tt> instance.
     *
     * @param className fully qualified name of the class this factory will
     *                  create
     */
    public ReflectionFactory(String className) {
        if (className == null) {
            throw new IllegalArgumentException(
                    "Class name cannot be null");
        }

        this.className = className;
    }

    // ---- Factory implementation ------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public T create() {
        try {
            return (T) Thread.currentThread().getContextClassLoader().loadClass(className).newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs during deserialization
     */
    @Override
    public void readExternal(PofReader reader)
            throws IOException {
        className = reader.readString(0);
    }

    /**
     * Serialize this object into a POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs during serialization
     */
    @Override
    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeString(0, className);
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
        return super.equals(o)
               && className.equals(((ReflectionFactory) o).className);
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return className.hashCode();
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "ReflectionFactory{" +
               "className='" + className + '\'' +
               '}';
    }
}
