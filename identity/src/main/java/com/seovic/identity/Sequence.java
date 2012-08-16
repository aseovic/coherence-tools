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

package com.seovic.identity;


import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import java.io.IOException;
import java.io.Serializable;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * Represents a named sequence.
 *
 * @author Aleksandar Seovic  2009.05.27
 */
@Entity
@Table(name = "SEQUENCES")
@Access(AccessType.FIELD)
public class Sequence
        implements Serializable, PortableObject {

    private static final long serialVersionUID = 6583656032091437643L;

    // ---- data members ----------------------------------------------------

    /**
     * Sequence name.
     */
    @Id
    @Column(name = "NAME", nullable = false)
    private String name;

    /**
     * The last allocated number from this sequence.
     */
    @Column(name = "LAST_SEQ", nullable = false)
    private long last;

    // ---- constructors --------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public Sequence() {
    }

    /**
     * Sequence constructor.
     *
     * @param name sequence name
     */
    public Sequence(String name) {
        this.name = name;
    }

    /**
     * Sequence constructor.
     *
     * @param name sequence name
     * @param last last assigned number
     */
    public Sequence(String name, long last) {
        this.name = name;
        this.last = last;
    }


    // ---- public methods --------------------------------------------------

    /**
     * Allocate a block of sequence numbers, starting from the last allocated
     * sequence value.
     *
     * @param blockSize the number of sequences to allocate
     *
     * @return allocated block of sequential numbers
     */
    public SequenceGenerator.SequenceBlock allocateBlock(int blockSize) {
        final long l = this.last;
        SequenceGenerator.SequenceBlock block =
                new SequenceGenerator.SequenceBlock(l + 1, l + blockSize);
        this.last = l + blockSize;

        return block;
    }

    /**
     * Return the sequence name.
     *
     * @return the sequence name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the last allocated sequence number.
     *
     * @return the last allocated sequence number
     */
    public long getLast() {
        return last;
    }


    // ---- PortableObject implementation -----------------------------------

    /**
     * Deserialize object from the POF stream.
     *
     * @param reader POF reader to use
     *
     * @throws IOException if an error occurs
     */
    public void readExternal(PofReader reader)
            throws IOException {
        name = reader.readString(0);
        last = reader.readLong(1);
    }

    /**
     * Serialize object into the POF stream.
     *
     * @param writer POF writer to use
     *
     * @throws IOException if an error occurs
     */
    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeString(0, name);
        writer.writeLong(1, last);
    }


    // ---- Object methods --------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Sequence sequence = (Sequence) o;

        return last == sequence.last
               && name.equals(sequence.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (int) (last ^ (last >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Sequence(" +
               "name='" + name + '\'' +
               ", last=" + last +
               ')';
    }
}
