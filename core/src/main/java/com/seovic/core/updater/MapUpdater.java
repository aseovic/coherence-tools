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
import java.util.Map;


/**
 * Simple imlementation of {@link Updater} that updates single map entry.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
public class MapUpdater
        implements Updater, PortableObject {
    // ---- data members ----------------------------------------------------

    private String key;


    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public MapUpdater() {
    }

    /**
     * Construct a <tt>MapUpdater</tt> instance.
     *
     * @param key the key of the entry to update
     */
    public MapUpdater(String key) {
        this.key = key;
    }


    // ---- Updater implementation ------------------------------------------

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    public void update(Object target, Object value) {
        if (target == null) {
            throw new IllegalArgumentException("Updater target cannot be null");
        }
        if (!(target instanceof Map)) {
            throw new IllegalArgumentException("Updater target is not a Map");
        }

        ((Map) target).put(key, value);
    }


    //---- PortableObject implementation -----------------------------------

    /**
     * Deserialize this object from a POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs during deserialization
     */
    public void readExternal(PofReader reader)
            throws IOException {
        key = reader.readString(0);
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
        writer.writeString(0, key);
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

        MapUpdater that = (MapUpdater) o;
        return key.equals(that.key);
    }

    /**
     * Return hash code for this object.
     *
     * @return this object's hash code
     */
    @Override
    public int hashCode() {
        return key.hashCode();
    }

    /**
     * Return string representation of this object.
     *
     * @return string representation of this object
     */
    @Override
    public String toString() {
        return "MapUpdater{" +
               "key='" + key + '\'' +
               '}';
    }
}