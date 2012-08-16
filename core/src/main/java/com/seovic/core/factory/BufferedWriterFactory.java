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
import java.io.BufferedWriter;
import java.io.Serializable;
import java.io.Writer;


/**
 * A {@link Factory} implementation that creates a BufferedWriter.
 *
 * @author Aleksandar Seovic  2009.11.06
 */
public class BufferedWriterFactory
        extends AbstractFactory<Writer> {

    private static final long serialVersionUID = -5669952117159383211L;

    // ---- data members ----------------------------------------------------

    /**
     * The writer factory whose product should be wrapped with a BufferedWriter.
     */
    private Factory<Writer> writerFactory;

    /**
     * Buffer size in bytes.
     */
    private int bufSize;

    // ---- constructors ----------------------------------------------------

    /**
     * Construct BufferedWriterFactory instance.
     *
     * @param writerFactory the writer factory whose product should be wrapped
     *                      with a BufferedWriter
     */
    public BufferedWriterFactory(Factory<Writer> writerFactory) {
        this(writerFactory, 0);
    }

    /**
     * Construct BufferedWriterFactory instance.
     *
     * @param writerFactory the writer factory whose product should be wrapped
     *                      with a BufferedWriter
     * @param bufSize       buffer size in bytes
     */
    public BufferedWriterFactory(Factory<Writer> writerFactory, int bufSize) {
        this.writerFactory = writerFactory;
        this.bufSize = bufSize;
    }


    // ---- Factory implementation ------------------------------------------

    /**
     * {@inheritDoc}
     */
    public Writer create() {
        return bufSize > 0
               ? new BufferedWriter(writerFactory.create(), bufSize)
               : new BufferedWriter(writerFactory.create());
    }
}