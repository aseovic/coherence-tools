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
import java.util.concurrent.atomic.AtomicLong;


/**
 * An abstract base class for {@link IdGenerator} implementations that generate
 * sequential Long identifiers.
 *
 * @author Aleksandar Seovic  2009.05.27
 */
public abstract class SequenceGenerator
        implements IdGenerator<Long>, Serializable {

    private static final long serialVersionUID = 2326984174200422273L;

    // ---- data members ----------------------------------------------------

    /**
     * Sequence name.
     */
    private final String name;

    /**
     * Sequence block size.
     */
    private final int blockSize;

    /**
     * Currently allocated block of sequences.
     */
    private SequenceBlock sequenceBlock;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct sequence generator.
     *
     * @param name      a sequence name
     * @param blockSize the size of the sequence block to allocate at once
     */
    protected SequenceGenerator(String name, int blockSize) {
        this.name = name;
        this.blockSize = blockSize;
    }


    // ---- properties ------------------------------------------------------

    public String getSequenceName() {
        return name;
    }

    public int getBlockSize() {
        return blockSize;
    }


    // ---- IdGenerator implementation --------------------------------

    /**
     * Return the next number in the sequence.
     *
     * @return the next number in the sequence
     */
    public synchronized Long generateId() {
        if (sequenceBlock == null || !sequenceBlock.hasNext()) {
            sequenceBlock = allocateSequenceBlock();
        }
        return sequenceBlock.next();
    }


    // ---- abstract methods --------------------------------------------------

    /**
     * Allocate a new sequence block.
     *
     * @return block of sequential numbers
     */
    protected abstract SequenceBlock allocateSequenceBlock();


    /**
     * Represents a block of sequential numbers.
     *
     * @author Aleksandar Seovic  2009.05.27
     */
    public static class SequenceBlock
            implements Serializable, PortableObject {

        private static final long serialVersionUID = -287204749575096536L;

        // ---- data members ----------------------------------------------------

        /**
         * The next assignable number within this sequence block.
         */
        private AtomicLong next;

        /**
         * The last assignable number within this sequence block.
         */
        private volatile long last;

        // ---- constructors ----------------------------------------------------

        /**
         * Deserialization constructor (for internal use only).
         */
        public SequenceBlock() {
        }

        /**
         * Construct a new sequence block.
         *
         * @param first first number in a sequence
         * @param last  last number in a sequence
         */
        public SequenceBlock(long first, long last) {
            this.next = new AtomicLong(first);
            this.last = last;
        }


        // ---- public methods --------------------------------------------------

        /**
         * Return <tt>true</tt> if there are avialable numbers in this sequence
         * block, <tt>false</tt> otherwise.
         *
         * @return <tt>true</tt> if there are avialable numbers in this sequence
         *         block, <tt>false</tt> otherwise
         */
        public boolean hasNext() {
            return next.longValue() <= last;
        }

        /**
         * Return the next available number in this sequence block.
         *
         * @return the next available number in this sequence block
         */
        public long next() {
            return next.getAndIncrement();
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
            next = new AtomicLong(reader.readLong(0));
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
            writer.writeLong(0, next.longValue());
            writer.writeLong(1, last);
        }
    }
}
