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


import com.tangosol.io.ReadBuffer;
import com.tangosol.io.WriteBuffer;
import com.tangosol.io.pof.PofBufferReader;
import com.tangosol.io.pof.PofBufferWriter;
import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofSerializer;

import java.io.IOException;


/**
 * @author Aleksandar Seovic  2012.05.08
 */
public abstract class HierarchicalPofContext implements PofContext {
    private PofContext parent;

    protected HierarchicalPofContext() {
    }

    protected HierarchicalPofContext(PofContext parent) {
        this.parent = parent;
    }

    protected void setParent(PofContext parentCtx) {
        parent = parentCtx;
    }

    protected abstract PofSerializer getPofSerializerInternal(int typeId);
    protected abstract int getUserTypeIdentifierInternal(Class aClass);
    protected abstract int getUserTypeIdentifierInternal(String className);
    protected abstract String getClassNameInternal(int typeId);
    protected abstract Class getClassInternal(int typeId);
    protected abstract boolean isUserTypeInternal(Class aClass);
    protected abstract boolean isUserTypeInternal(String className);


    public PofSerializer getPofSerializer(int typeId) {
        PofSerializer serializer = getPofSerializerInternal(typeId);
        return serializer == null ? parent.getPofSerializer(typeId) : serializer;
    }

    public int getUserTypeIdentifier(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        return getUserTypeIdentifier(o.getClass());
    }

    public int getUserTypeIdentifier(Class aClass) {
        int typeId = getUserTypeIdentifierInternal(aClass);
        return typeId == -1 ? parent.getUserTypeIdentifier(aClass) : typeId;
    }

    public int getUserTypeIdentifier(String className) {
        int typeId = getUserTypeIdentifierInternal(className);
        return typeId == -1 ? parent.getUserTypeIdentifier(className) : typeId;
    }

    public String getClassName(int typeId) {
        String className = getClassNameInternal(typeId);
        return className == null ? parent.getClassName(typeId) : className;
    }

    public Class getClass(int typeId) {
        Class cls = getClassInternal(typeId);
        return cls == null ? parent.getClass(typeId) : cls;
    }

    public boolean isUserType(Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }

        return isUserType(o.getClass());
    }

    public boolean isUserType(Class aClass) {
        return isUserTypeInternal(aClass) || parent.isUserType(aClass);
    }

    public boolean isUserType(String className) {
        return isUserTypeInternal(className) || parent.isUserType(className);
    }

    @Override
    public void serialize(WriteBuffer.BufferOutput out, Object o) throws IOException {
        PofBufferWriter writer = new PofBufferWriter(out, this);

        try
        {
            writer.writeObject(-1, o);
        }
        catch (RuntimeException e)
        {
            IOException ex = new IOException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
    }

    @Override
    public Object deserialize(ReadBuffer.BufferInput in) throws IOException {
        PofBufferReader reader = new PofBufferReader(in, this);

        try
        {
            return reader.readObject(-1);
        }
        catch (RuntimeException e)
        {
            IOException ex = new IOException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
    }
}
