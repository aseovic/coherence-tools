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


import com.seovic.pof.annotations.PortableType;

import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofSerializer;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.util.Binary;

import java.io.IOException;

import java.lang.reflect.Method;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Aleksandar Seovic  2012.05.27
 */
@SuppressWarnings("unchecked")
public class PortableTypeSerializer
        implements PofSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(PortableTypeSerializer.class);
    private static final Map<Class, Method> READ_EXTERNAL_CACHE  = new ConcurrentHashMap<Class, Method>();
    private static final Map<Class, Method> WRITE_EXTERNAL_CACHE = new ConcurrentHashMap<Class, Method>();
    private static final Map<Class, PortableType> PORTABLE_TYPE_CACHE = new ConcurrentHashMap<Class, PortableType>();

    private int typeId;

    public PortableTypeSerializer(int typeId, Class<?> clz) {
        if (!clz.isAnnotationPresent(PortableType.class)) {
            LOG.error("Class [" + clz + "] does not have @PortableType annotation");
        }
        this.typeId = typeId;
    }

    @Override
    public void serialize(PofWriter out, Object o)
            throws IOException {

        if (getPortableTypeAnnotation(o.getClass()) == null) {
            throw new IOException("Class [" + o.getClass() + "] does not have @PortableType annotation");
        }

        boolean fEvolvable = o instanceof EvolvableObject;
        EvolvableObject e = fEvolvable ? (EvolvableObject) o : null;

        try {
            LOG.trace("Serializing " + o.getClass());

            PofContext ctx = out.getPofContext();
            Set<Integer> typeIds;
            if (fEvolvable) {
                refreshVersions(e, ctx);
                out.writeMap(0, e.getVersions(), Integer.class, Integer.class);
                typeIds = e.getVersions().keySet();
            }
            else {
                typeIds = getTypeIds(o, ctx);
            }

            for (int typeId : typeIds) {
                PofWriter writer = out.createNestedPofWriter(typeId);

                Class cls = getClassForTypeId(ctx, typeId);
                if (cls != null) {
                    writer.setVersionId(fEvolvable ? e.getDataVersion(typeId) : getPortableTypeAnnotation(cls).version());

                    Method writeExternal = getWriteExternal(cls);
                    writeExternal.invoke(o, writer);
                }

                writer.writeRemainder(fEvolvable ? e.getFutureData(typeId) : null);
            }

            out.writeRemainder(null);
        }
        catch (Exception ex) {
            throw new IOException("An exception occurred during serialization", ex);
        }
    }

    @Override
    public Object deserialize(PofReader in)
            throws IOException {

        try {
            PofContext ctx = in.getPofContext();
            Object o = createInstance(getClassForTypeId(in.getPofContext(), typeId));
            LOG.trace("Deserializing " + o.getClass());

            boolean fEvolvable = o instanceof EvolvableObject;
            EvolvableObject e = fEvolvable ? (EvolvableObject) o : null;
            Set<Integer> typeIds;
            if (fEvolvable) {
                in.readMap(0, e.getVersions());
                typeIds = e.getVersions().keySet();
            }
            else {
                typeIds = getTypeIds(o, ctx);
            }

            for (int typeId : typeIds) {
                PofReader reader = in.createNestedPofReader(typeId);
                int versionId = reader.getVersionId();
                if (fEvolvable) {
                    e.setDataVersion(typeId, versionId);
                }

                Class cls = getClassForTypeId(ctx, typeId);
                if (cls != null) {
                    Method readExternal = getReadExternal(cls);
                    readExternal.invoke(o, reader);
                }

                Binary remainder = reader.readRemainder();
                if (fEvolvable) {
                    e.setFutureData(typeId, remainder);
                }
            }

            in.readRemainder();
            return o;
        }
        catch (Exception ex) {
            throw new IOException("An exception occurred during deserialization", ex);
        }
    }

    private Method getWriteExternal(Class cls) throws NoSuchMethodException {
        Method method = WRITE_EXTERNAL_CACHE.get(cls);
        if (method == null) {
            method = cls.getDeclaredMethod("writeExternal", PofWriter.class);
            method.setAccessible(true);
            WRITE_EXTERNAL_CACHE.put(cls, method);
        }
        return method;
    }

    private Method getReadExternal(Class cls) throws NoSuchMethodException {
        Method method = READ_EXTERNAL_CACHE.get(cls);
        if (method == null) {
            method = cls.getDeclaredMethod("readExternal", PofReader.class);
            method.setAccessible(true);
            READ_EXTERNAL_CACHE.put(cls, method);
        }
        return method;
    }

    private PortableType getPortableTypeAnnotation(Class cls) {
        if (cls.equals(Object.class) || cls.equals(AbstractEvolvableObject.class)) {
            return null;
        }

        PortableType pt = PORTABLE_TYPE_CACHE.get(cls);
        if (pt == null) {
            pt = (PortableType) cls.getAnnotation(PortableType.class);
            if (pt != null) {
                PORTABLE_TYPE_CACHE.put(cls, pt);
            }
        }
        return pt;
    }

    private Class getClassForTypeId(PofContext ctx, int typeId) {
        try {
            return ctx.getClass(typeId);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    private Set<Integer> getTypeIds(Object o, PofContext pofContext) {
        Set<Integer> typeIds = new TreeSet<Integer>();

        Class clazz = o.getClass();
        while (getPortableTypeAnnotation(clazz) != null) {
            typeIds.add(pofContext.getUserTypeIdentifier(clazz));
            clazz = clazz.getSuperclass();
        }

        return typeIds;
    }

    private void refreshVersions(EvolvableObject e, PofContext pofContext) {
        Class clazz = e.getClass();
        PortableType portableType;
        while ((portableType = getPortableTypeAnnotation(clazz)) != null) {
            int typeId  = pofContext.getUserTypeIdentifier(clazz);
            int version = Math.max(portableType.version(), e.getDataVersion(typeId));

            e.setDataVersion(typeId, version);
            clazz = clazz.getSuperclass();
        }
    }

    private <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException("Cannot create object", e);
        }
    }
}
