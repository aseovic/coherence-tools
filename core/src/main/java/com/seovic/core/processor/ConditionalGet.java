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
import com.tangosol.util.Filter;
import com.tangosol.util.InvocableMap;
import com.tangosol.util.processor.AbstractProcessor;
import java.io.IOException;
import java.io.Serializable;


/**
 * Return entry value only if it satisfies specified condition.
 *
 * @author Aleksandar Seovic  2011.04.11
 */
public class ConditionalGet
        extends AbstractProcessor
        implements Serializable, PortableObject {

    private static final long serialVersionUID = -5040290222247708531L;

    // ---- data members ----------------------------------------------------

    private Filter condition;

    // ---- constructors ----------------------------------------------------

    public ConditionalGet() {
    }

    public ConditionalGet(Filter condition) {
        this.condition = condition;
    }

    // ----- AbstractProcessor implementation -------------------------------

    @Override
    public Object process(InvocableMap.Entry entry) {
        Object value = entry.getValue();
        return condition.evaluate(value) ? value : null;
    }

    // ---- PortableObject implementation -----------------------------------

    @Override
    public void readExternal(PofReader reader)
            throws IOException {
        condition = (Filter) reader.readObject(0);
    }

    @Override
    public void writeExternal(PofWriter writer)
            throws IOException {
        writer.writeObject(0, condition);
    }
}
