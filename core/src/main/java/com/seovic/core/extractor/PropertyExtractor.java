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
import java.lang.reflect.Method;


/**
 * Simple implementation of {@link Extractor} that extracts value from a target
 * object using introspection.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
@SuppressWarnings("unchecked")
public class PropertyExtractor<T>
        implements Extractor<T>, Serializable, PortableObject {

    private static final long serialVersionUID = -6077207711822880508L;

    private static final String[] PREFIXES = new String[] {"get", "is"};

    // ---- data members ----------------------------------------------------

    /**
     * Property name.
     */
    private String propertyName;

    /**
     * Property accessor.
     */
    private transient Method propertyAccessor;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public PropertyExtractor() {
    }

    /**
     * Construct a <tt>BeanExtractor</tt> instance.
     *
     * @param propertyName the name of the property to extract, as defined by
     *                     the JavaBean specification
     */
    public PropertyExtractor(String propertyName) {
        this.propertyName = propertyName;
    }


    // ---- Extractor implementation ----------------------------------------

    /**
     * {@inheritDoc}
     */
    public T extract(Object target) {
        if (target == null) {
            return null;
        }

        Class targetClass = target.getClass();
        try {
            if (propertyAccessor == null || propertyAccessor.getDeclaringClass() != targetClass) {
                propertyAccessor = findReadMethod(propertyName, target.getClass());
            }

            if (propertyAccessor != null) {
                return (T) propertyAccessor.invoke(target);
            }
            else {
                throw new RuntimeException("Readable property " + propertyName +
                                           " does not exist in the class "
                                           + targetClass);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // ---- helper methods --------------------------------------------------

    /**
     * Attempt to find a read method for the specified property name.
     * <p/>
     * This method attempts to find a read method by prepending prefixes 'get'
     * and 'is' to the specified property name, in that order.
     *
     * @param propertyName property name
     * @param cls          class containing the property
     *
     * @return read method for the property, or <tt>null</tt> if the method
     *         cannot be found
     */
    protected Method findReadMethod(String propertyName, Class cls) {
        String name = Character.toUpperCase(propertyName.charAt(0))
                      + propertyName.substring(1);

        for (String prefix : PREFIXES) {
            try {
                return cls.getMethod(prefix + name);
            }
            catch (NoSuchMethodException ignore) {
            }
        }

        return null;
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
        propertyName = reader.readString(0);
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
        writer.writeString(0, propertyName);
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

        PropertyExtractor that = (PropertyExtractor) o;
        return propertyName.equals(that.propertyName);
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return propertyName.hashCode();
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "PropertyExtractor{" +
               "propertyName='" + propertyName + '\'' +
               '}';
    }
}
