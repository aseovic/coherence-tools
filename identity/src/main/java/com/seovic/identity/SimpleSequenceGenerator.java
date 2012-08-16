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


/**
 * Simplest possible implementation of {@link SequenceGenerator} that stores
 * sequence information locally, in memory.
 * <p/>
 * This {@link SequenceGenerator} implementation is not suitable for use when
 * the last assigned sequence number needs to be persisted across program
 * executions.
 * <p/>
 * In general, this implementation should only be used for testing.
 *
 * @author Aleksandar Seovic  2010.02.05
 */
public class SimpleSequenceGenerator
        extends SequenceGenerator {

    private static final long serialVersionUID = 2457224184284495515L;

    // ---- data members ----------------------------------------------------

    private final Sequence sequence;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct sequence generator.
     *
     * @param name      sequence name
     * @param blockSize the size of the sequence block to allocate at once
     */
    public SimpleSequenceGenerator(String name, int blockSize) {
        super(name, blockSize);
        sequence = new Sequence(name);
    }


    // ---- SequenceGenerator implementation --------------------------------

    /**
     * Allocate a new sequence block.
     *
     * @return block of sequential numbers
     */
    protected SequenceBlock allocateSequenceBlock() {
        return sequence.allocateBlock(getBlockSize());
    }
}
