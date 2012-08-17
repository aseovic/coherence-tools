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
import com.tangosol.util.InvocableMap;
import com.tangosol.util.extractor.ReflectionExtractor;
import com.tangosol.util.processor.AbstractProcessor;

import java.io.IOException;
import java.io.Serializable;


/**
 * @author Aleksandar Seovic  2010.11.06
 */
public class MethodInvocationProcessor
        extends AbstractProcessor
        implements Serializable, PortableObject {

    private static final long serialVersionUID = -4086874208377932078L;

    // ---- data members ----------------------------------------------------

    private String name;
    private boolean mutator;
    private Object[] args;

    // ---- constructors ----------------------------------------------------

    public MethodInvocationProcessor() {
    }

    public MethodInvocationProcessor(String name, boolean mutator,
                                     Object... args) {
        this.name = name;
        this.mutator = mutator;
        this.args = args;
    }

    // ----- AbstractProcessor implementation -------------------------------

    @Override
    public Object process(InvocableMap.Entry entry) {
        ReflectionExtractor extractor = new ReflectionExtractor(name, args);
        if (mutator) {
            Object value = entry.getValue();
            Object result = extractor.extract(value);
            entry.setValue(value);
            return result;
        }
        else {
            return entry.extract(extractor);
        }
    }

    // ---- PortableObject implementation -----------------------------------

    @Override
    public void readExternal(PofReader reader)
            throws IOException {
        name = reader.readString(0);
        mutator = reader.readBoolean(1);
        args = reader.readObjectArray(2, new Object[0]);
    }

    @Override
    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeString(0, name);
        writer.writeBoolean(1, mutator);
        writer.writeObjectArray(2, args);
    }
}
