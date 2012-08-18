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

package com.seovic.pof;


import com.tangosol.io.Evolvable;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.Binary;
import java.io.IOException;


/**
 * Base class for custom POF serializers that supports schema evolution through
 * <tt>Evolvable</tt> interface.
 *
 * @author Aleksandar Seovic  2008.10.25
 */
@SuppressWarnings("unchecked")
public abstract class AbstractPofSerializer<T>
        implements PofSerializer
    {
    // ---- abstract members ----------------------------------------------------

    /**
     * Serialize object attributes.
     *
     * @param obj     object to serialize
     * @param writer  PofWriter to use
     *
     * @throws IOException  if an error occurs during serialization
     */
    protected abstract void serializeAttributes(T obj, PofWriter writer)
            throws IOException;

    /**
     * Create object instance during deserialization.
     * <p/>
     * This method should only read attributes from the POF stream that are
     * absolutely necessary to create object instance. The remaining attributes
     * should be read within the {@link #deserializeAttributes} method.
     *
     * @param reader  PofReader to use
     *
     * @return an instance of the, possibly partially, deserialized object
     *
     * @throws IOException if an error occurs during deserialization
     */
    protected abstract T createInstance(PofReader reader)
            throws IOException;

    /**
     * Deserialize object attributes.
     *
     * @param obj     object instance to set attributes on
     * @param reader  PofReader to use
     *
     * @throws IOException  if an error occurs during deserialization
     */
    @SuppressWarnings("UnusedDeclaration")
    protected void deserializeAttributes(T obj, PofReader reader)
            throws IOException
        {
        // default empty implementation
        }


    // ---- PofSerializer implementation ------------------------------------

    /**
     * {@inheritDoc}
     */
    public void serialize(PofWriter writer, Object obj)
            throws IOException
        {
        T         instance    = (T) obj;
        boolean   isEvolvable = obj instanceof Evolvable;
        Evolvable evolvable   = null;

        if (isEvolvable)
            {
            evolvable = (Evolvable) obj;
            int dataVersion = Math.max(
                    evolvable.getImplVersion(),
                    evolvable.getDataVersion());
            writer.setVersionId(dataVersion);
            }

        serializeAttributes(instance, writer);

        Binary futureData = isEvolvable
                            ? evolvable.getFutureData()
                            : null;
        writer.writeRemainder(futureData);
        }

    /**
     * {@inheritDoc}
     */
    public Object deserialize(PofReader reader)
            throws IOException
        {
        T         instance    = createInstance(reader);
        boolean   isEvolvable = instance instanceof Evolvable;
        Evolvable evolvable   = null;

        if (isEvolvable)
            {
            evolvable = (Evolvable) instance;
            evolvable.setDataVersion(reader.getVersionId());
            }

        deserializeAttributes(instance, reader);

        Binary futureData = reader.readRemainder();
        if (isEvolvable)
            {
            evolvable.setFutureData(futureData);
            }

        return instance;
        }
    }
