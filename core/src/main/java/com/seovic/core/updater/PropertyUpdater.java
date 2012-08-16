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

package com.seovic.core.updater;


import com.seovic.core.Updater;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;


/**
 * Simple imlementation of {@link Updater} that updates single property of a
 * target value using introspection.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
@SuppressWarnings("unchecked")
public class PropertyUpdater
        implements Updater, Serializable, PortableObject {

    private static final long serialVersionUID = 681827826766741360L;

    // ---- data members ----------------------------------------------------

    /**
     * Property name.
     */
    private String propertyName;


    /**
     * Property accessor.
     */
    private transient Method propertyMutator;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public PropertyUpdater() {
    }

    /**
     * Construct a <tt>BeanExtractor</tt> instance.
     *
     * @param propertyName the name of the property to update, as defined by the
     *                     JavaBean specification
     */
    public PropertyUpdater(String propertyName) {
        this.propertyName = propertyName;
    }


    // ---- Updater implementation ------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void update(Object target, Object value) {
        if (target == null) {
            throw new IllegalArgumentException("Updater target cannot be null");
        }

        Class targetClass = target.getClass();
        try {
            if (propertyMutator == null
                || propertyMutator.getDeclaringClass() != targetClass) {
                propertyMutator = findWriteMethod(propertyName,
                                                  target.getClass(),
                                                  value == null
                                                    ? Object.class
                                                    : value.getClass());
            }
            if (propertyMutator != null) {
                propertyMutator.invoke(target, value);
            }
            else {
                throw new RuntimeException("Writeable property " + propertyName +
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
     * Attempt to find a write method for the specified property name.
     * <p/>
     * This method attempts to find a write method by prepending 'set' prefix to
     * the specified property name.
     *
     * @param propertyName property name
     * @param cls          class containing the property
     * @param propertyType property type
     *
     * @return write method for the property, or <tt>null</tt> if the method
     *         cannot be found
     */
    protected Method findWriteMethod(String propertyName, Class cls,
                                     Class propertyType) {
        String name = "set"
                      + Character.toUpperCase(propertyName.charAt(0))
                      + propertyName.substring(1);

        try {
            return cls.getMethod(name, propertyType);
        }
        catch (NoSuchMethodException ignore) {
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

        PropertyUpdater that = (PropertyUpdater) o;
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
        return "PropertyUpdater{" +
               "propertyName='" + propertyName + '\'' +
               '}';
    }
}