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


import com.seovic.core.Factory;
import com.seovic.core.io.JAXBMarshaller;
import com.seovic.pof.annotations.Portable;
import com.seovic.pof.annotations.PortableArray;
import com.seovic.pof.annotations.PortableDate;
import com.seovic.pof.annotations.PortableList;
import com.seovic.pof.annotations.PortableMap;
import com.seovic.pof.annotations.PortableSet;
import com.seovic.pof.annotations.PortableType;
import com.seovic.pof.annotations.internal.Instrumented;
import com.seovic.pof.annotations.internal.PofIndex;
import com.seovic.pof.internal.PofConfig;
import com.seovic.pof.internal.SerializerType;
import com.seovic.pof.internal.UserType;
import com.seovic.pof.internal.UserTypeList;
import com.seovic.pof.util.AsmUtils;

import com.tangosol.io.pof.PofSerializer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MemberNode;
import org.objectweb.asm.tree.MethodNode;
import com.tangosol.util.Binary;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.ProtectionDomain;
import java.sql.Timestamp;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.LoggerFactory;

import static org.objectweb.asm.Opcodes.*;


/**
 * @author Aleksandar Seovic  2012.05.28
 */
@SuppressWarnings("unchecked")
public class PortableTypeGenerator {
    public static Logger LOG = new ConsoleLogger();
    private static final Type OBJECT_TYPE = Type.getType(Object.class);

    protected static final Class[] PORTABLE_ANNOTATIONS = new Class[]{
            Portable.class, PortableArray.class, PortableDate.class, PortableList.class, PortableMap.class, PortableSet.class};

    private static final Set<String> MAP_CLASSES = new HashSet<String>(Arrays.asList(
            Map.class.getName(), ConcurrentMap.class.getName(), ConcurrentNavigableMap.class.getName(),
            NavigableMap.class.getName(), SortedMap.class.getName(), ConcurrentHashMap.class.getName(),
            ConcurrentSkipListMap.class.getName(), HashMap.class.getName(), IdentityHashMap.class.getName(),
            LinkedHashMap.class.getName(), TreeMap.class.getName(), WeakHashMap.class.getName(), Hashtable.class.getName()
    ));
    private static final Set<String> SET_CLASSES = new HashSet<String>(Arrays.asList(
            Set.class.getName(), SortedSet.class.getName(), NavigableSet.class.getName(), ConcurrentSkipListSet.class.getName(),
            CopyOnWriteArraySet.class.getName(), HashSet.class.getName(), LinkedHashSet.class.getName(), TreeSet.class.getName()

    ));
    private static final Set<String> LIST_CLASSES = new HashSet<String>(Arrays.asList(
            List.class.getName(), ArrayList.class.getName(), CopyOnWriteArrayList.class.getName(), LinkedList.class.getName()
    ));

    private static final Set<String> COLLECTION_CLASSES = merge(new HashSet<String>(Arrays.asList(
            Collection.class.getName(), Queue.class.getName(), Deque.class.getName(), BlockingQueue.class.getName(), BlockingDeque.class.getName(),
            ArrayDeque.class.getName(), ArrayBlockingQueue.class.getName(), ConcurrentLinkedQueue.class.getName(),
            DelayQueue.class.getName(), LinkedBlockingQueue.class.getName(), LinkedBlockingDeque.class.getName(),
            PriorityBlockingQueue.class.getName(), PriorityQueue.class.getName(),
            Stack.class.getName(), SynchronousQueue.class.getName(), Vector.class.getName()
    )), SET_CLASSES, LIST_CLASSES);

    private static <T> Set<T> merge(Set<T>... sets) {
        Set<T> result = new HashSet<T>();
        for (Set<T> set : sets) {
            result.addAll(set);
        }
        return result;
    }

    public static boolean DEBUG = false;

    private ClassNode cn;
    private TreeMap<Integer, SortedSet<FieldNode>> properties = new TreeMap<Integer, SortedSet<FieldNode>>();

    public PortableTypeGenerator(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        cn = new ClassNode();
        reader.accept(cn, 0);
    }

    public UserType instrumentClass() {
        if (isPortableType() && !isEnum() && !isInstrumented()) {
            LOG.info("Instrumenting portable type " + cn.name);

            populatePropertyMap();
            implementDefaultConstructor();
            implementReadExternal();
            implementWriteExternal();

            // mark as instrumented
            cn.visibleAnnotations.add(new AnnotationNode(Type.getDescriptor(Instrumented.class)));

            Type serializerClass = getPofSerializer();
            SerializerType serializerType = serializerClass.getClassName().equals(PortableTypeSerializer.class.getName())
                                            ? null
                                            : new SerializerType(serializerClass.getClassName(), null);

            return new UserType(BigInteger.valueOf(getTypeId()),
                                cn.name.replace("/", "."),
                                serializerType);
        }

        return null;
    }

    private int getTypeId() {
        return (Integer) getAnnotationAttribute(getAnnotation(cn, PortableType.class), "id");
    }

    private Type getPofSerializer() {
        return (Type) getAnnotationAttribute(getAnnotation(cn, PortableType.class), "serializer");
    }

    public byte[] getClassBytes() {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(writer);
        return writer.toByteArray();
    }

    public void writeClass(OutputStream out) throws IOException {
        out.write(getClassBytes());
    }

    private void populatePropertyMap() {
        int count = 0;
        for (FieldNode fn : ((List<FieldNode>) cn.fields)) {
            AnnotationNode an = getAnnotation(fn, PORTABLE_ANNOTATIONS);
            if (an != null) {
                addProperty((Integer) getAnnotationAttribute(an, "since"), fn);
                count++;
            }
        }
        LOG.debug("Found " + count + " fields across " + properties.size() + " class version(s)");
    }

    private void addProperty(int version, FieldNode property) {
        SortedSet<FieldNode> nodes = properties.get(version);
        if (nodes == null) {
            nodes = new TreeSet<FieldNode>(new FieldNodeComparator());
            properties.put(version, nodes);
        }
        nodes.add(property);
    }

    private boolean isPortableType() {
        return hasAnnotation(cn, PortableType.class);
    }

    private boolean isEnum() {
        return (cn.access & ACC_ENUM) == ACC_ENUM;
    }

    private boolean isInstrumented() {
        return hasAnnotation(cn, Instrumented.class);
    }

    private boolean hasAnnotation(MemberNode node, Class annotationClass) {
        return getAnnotation(node, annotationClass) != null;
    }

    private AnnotationNode getAnnotation(MemberNode node, Class... annotationClasses) {
        if (node.visibleAnnotations != null) {
            for (Class annotationClass : annotationClasses) {
                String desc = Type.getDescriptor(annotationClass);
                for (AnnotationNode an : (List<AnnotationNode>) node.visibleAnnotations) {
                    if (desc.equals(an.desc)) return an;
                }
            }
        }
        return null;
    }

    private Object getAnnotationAttribute(AnnotationNode an, String name) {
        if (an.values != null) {
            for (int i = 0; i < an.values.size(); i += 2) {
                if (name.equals(an.values.get(i))) {
                    Object value = an.values.get(i + 1);
                    if (value.getClass().isArray()) { // DateMode enum
                        String[] dateMode = (String[]) value;
                        return DateMode.valueOf(dateMode[1]);
                    }
                    return value;
                }
            }
        }

        // annotation attribute was not explicitly specified, use default from the annotation class
        try {
            Class clazz = getClass().getClassLoader().loadClass(an.desc.substring(1, an.desc.length() - 1).replace('/', '.'));
            Object defaultValue = clazz.getMethod(name).getDefaultValue();
            return defaultValue instanceof Class ? Type.getType((Class) defaultValue) : defaultValue;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void implementDefaultConstructor() {
        MethodNode ctor = findMethod("<init>", "()V");
        if (ctor == null) {
            ctor = new MethodNode(ACC_PUBLIC, "<init>", "()V", null, null);

            ctor.visitCode();
            ctor.visitVarInsn(ALOAD, 0);
            ctor.visitMethodInsn(INVOKESPECIAL, cn.superName, "<init>", "()V");
            ctor.visitInsn(RETURN);
            ctor.visitMaxs(1, 1);
            ctor.visitEnd();

            cn.methods.add(ctor);
        }
        else if ((ctor.access & ACC_PUBLIC) == 0) {
            LOG.info("Class " + cn.name + " has a non-public default constructor. Making it public.");
            ctor.access = ACC_PUBLIC;
        }
    }

    private void implementReadExternal() {
        int index = 0;

        MethodNode mn = new MethodNode(ACC_PRIVATE, "readExternal", "(Lcom/tangosol/io/pof/PofReader;)V", null, new String[]{"java/io/IOException"});
        mn.visitCode();

        for (int version : properties.keySet()) {
            mn.visitVarInsn(ALOAD, 1);
            mn.visitMethodInsn(INVOKEINTERFACE, "com/tangosol/io/pof/PofReader", "getVersionId", "()I");
            mn.visitIntInsn(BIPUSH, version);
            Label l = new Label();
            mn.visitJumpInsn(IF_ICMPLT, l);

            SortedSet<FieldNode> fields = properties.get(version);
            for (FieldNode fn : fields) {
                Type type = Type.getType(fn.desc);

                if (isDebugEnabled()) {
                    mn.visitLdcInsn("reading attribute " + index + " (" + fn.name + ") from POF stream");
                    mn.visitIntInsn(BIPUSH, 7);
                    mn.visitMethodInsn(INVOKESTATIC, "com/tangosol/net/CacheFactory", "log", "(Ljava/lang/String;I)V");
                }
                mn.visitVarInsn(ALOAD, 0);
                mn.visitVarInsn(ALOAD, 1);
                mn.visitIntInsn(BIPUSH, index++);

                ReadMethod readMethod = getReadMethod(fn, type);
                readMethod.createTemplate(mn, fn, type);
                mn.visitMethodInsn(INVOKEINTERFACE, "com/tangosol/io/pof/PofReader", readMethod.getName(), readMethod.getDescriptor());
                if (type.getSort() == Type.OBJECT || "readObjectArray".equals(readMethod.getName())) {
                    mn.visitTypeInsn(CHECKCAST, type.getInternalName());
                }
                mn.visitFieldInsn(PUTFIELD, cn.name, fn.name, fn.desc);
            }

            mn.visitLabel(l);
            mn.visitFrame(F_SAME, 0, null, 0, null);
        }

        mn.visitInsn(RETURN);
        mn.visitMaxs(0, 0);
        mn.visitEnd();

        if (!hasMethod(mn)) {
            cn.methods.add(mn);
        }
        LOG.debug("Implemented method: " + mn.name);
    }

    private Type getFactoryType(AnnotationNode an) {
        Type factoryClass = (Type) getAnnotationAttribute(an, "factory");
        return !factoryClass.equals(Type.getType(Factory.class))
               ? factoryClass
               : null;
    }

    private ReadMethod getReadMethod(FieldNode field, Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                return new ReadMethod("readBoolean", "(I)Z");
            case Type.BYTE:
                return new ReadMethod("readByte", "(I)B");
            case Type.CHAR:
                return new ReadMethod("readChar", "(I)C");
            case Type.SHORT:
                return new ReadMethod("readShort", "(I)S");
            case Type.INT:
                return new ReadMethod("readInt", "(I)I");
            case Type.LONG:
                return new ReadMethod("readLong", "(I)J");
            case Type.FLOAT:
                return new ReadMethod("readFloat", "(I)F");
            case Type.DOUBLE:
                return new ReadMethod("readDouble", "(I)D");

            case Type.ARRAY:
                if ("[Z".equals(type.getDescriptor())) return new ReadMethod("readBooleanArray", "(I)[Z");
                if ("[B".equals(type.getDescriptor())) return new ReadMethod("readByteArray", "(I)[B");
                if ("[C".equals(type.getDescriptor())) return new ReadMethod("readCharArray", "(I)[C");
                if ("[S".equals(type.getDescriptor())) return new ReadMethod("readShortArray", "(I)[S");
                if ("[I".equals(type.getDescriptor())) return new ReadMethod("readIntArray", "(I)[I");
                if ("[J".equals(type.getDescriptor())) return new ReadMethod("readLongArray", "(I)[J");
                if ("[F".equals(type.getDescriptor())) return new ReadMethod("readFloatArray", "(I)[F");
                if ("[D".equals(type.getDescriptor())) return new ReadMethod("readDoubleArray", "(I)[D");
                return new ObjectArrayReadMethod();

            default:
                if (type.getClassName().equals(String.class.getName()))     return new ReadMethod("readString", "(I)Ljava/lang/String;");
                if (type.getClassName().equals(Date.class.getName()))       return new ReadMethod("readDate", "(I)Ljava/util/Date;");
                if (type.getClassName().equals(BigDecimal.class.getName())) return new ReadMethod("readBigDecimal", "(I)Ljava/math/BigDecimal;");
                if (type.getClassName().equals(BigInteger.class.getName())) return new ReadMethod("readBigInteger", "(I)Ljava/math/BigInteger;");
                if (type.getClassName().equals(Binary.class.getName()))     return new ReadMethod("readBinary", "(I)Lcom/tangosol/util/Binary;");
                if (isCollection(field, type))                              return new CollectionReadMethod();
                if (isMap(field, type))                                     return new MapReadMethod();
                return new ReadMethod("readObject", "(I)Ljava/lang/Object;");
        }
    }

    private void implementWriteExternal() {
        int index = 0;

        MethodNode mn = new MethodNode(ACC_PRIVATE, "writeExternal", "(Lcom/tangosol/io/pof/PofWriter;)V", null, new String[]{"java/io/IOException"});
        mn.visitCode();

        for (int version : properties.keySet()) {
            SortedSet<FieldNode> fields = properties.get(version);
            for (FieldNode fn : fields) {
                addPofIndex(fn, index);
                Type type = Type.getType(fn.desc);

                if (isDebugEnabled()) {
                    mn.visitLdcInsn("writing attribute " + index + " (" + fn.name + ") to POF stream");
                    mn.visitIntInsn(BIPUSH, 7);
                    mn.visitMethodInsn(INVOKESTATIC, "com/tangosol/net/CacheFactory", "log", "(Ljava/lang/String;I)V");
                }
                mn.visitVarInsn(ALOAD, 1);
                mn.visitIntInsn(BIPUSH, index++);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitFieldInsn(GETFIELD, cn.name, fn.name, fn.desc);

                WriteMethod writeMethod = getWriteMethod(fn, type);
                writeMethod.pushUniformTypes(mn);
                mn.visitMethodInsn(INVOKEINTERFACE, "com/tangosol/io/pof/PofWriter", writeMethod.getName(), writeMethod.getDescriptor());
            }
        }

        mn.visitInsn(RETURN);
        mn.visitMaxs(0, 0);
        mn.visitEnd();

        if (!hasMethod(mn)) {
            cn.methods.add(mn);
        }
        LOG.debug("Implemented method: " + mn.name);
    }

    private MethodNode findMethod(String name, String desc) {
        for (MethodNode node : (List<MethodNode>) cn.methods) {
            if (node.name.equals(name) && node.desc.equals(desc))
                return node;
        }
        return null;
    }

    private boolean hasMethod(MethodNode mn) {
        for (MethodNode node : (List<MethodNode>) cn.methods) {
            if (mn.name.equals(node.name) && mn.desc.equals(node.desc))
                return true;
        }
        return false;
    }

    private boolean isDebugEnabled() {
        return DEBUG;
    }

    private void addPofIndex(FieldNode fn, int index) {
        AnnotationNode an = new AnnotationNode(Type.getDescriptor(PofIndex.class));
        an.values = Arrays.asList("value", index);
        fn.visibleAnnotations.add(an);
    }

    private WriteMethod getWriteMethod(FieldNode field, Type type) {
        switch (type.getSort()) {
            case Type.BOOLEAN:
                return new WriteMethod("writeBoolean", "(IZ)V");
            case Type.BYTE:
                return new WriteMethod("writeByte", "(IB)V");
            case Type.CHAR:
                return new WriteMethod("writeChar", "(IC)V");
            case Type.DOUBLE:
                return new WriteMethod("writeDouble", "(ID)V");
            case Type.FLOAT:
                return new WriteMethod("writeFloat", "(IF)V");
            case Type.INT:
                return new WriteMethod("writeInt", "(II)V");
            case Type.LONG:
                return new WriteMethod("writeLong", "(IJ)V");
            case Type.SHORT:
                return new WriteMethod("writeShort", "(IS)V");

            case Type.ARRAY:
                if ("[Z".equals(type.getDescriptor())) return new WriteMethod("writeBooleanArray", "(I[Z)V");
                if ("[B".equals(type.getDescriptor())) return new WriteMethod("writeByteArray", "(I[B)V");
                if ("[C".equals(type.getDescriptor())) return new WriteMethod("writeCharArray", "(I[C)V");
                if ("[D".equals(type.getDescriptor())) return new WriteMethod("writeDoubleArray", "(I[D)V");
                if ("[F".equals(type.getDescriptor())) return new WriteMethod("writeFloatArray", "(I[F)V");
                if ("[I".equals(type.getDescriptor())) return new WriteMethod("writeIntArray", "(I[I)V");
                if ("[J".equals(type.getDescriptor())) return new WriteMethod("writeLongArray", "(I[J)V");
                if ("[S".equals(type.getDescriptor())) return new WriteMethod("writeShortArray", "(I[S)V");
                return getObjectArrayWriteMethod(field, type);

            default: // Type.OBJECT
                if (type.getClassName().equals(String.class.getName()))     return new WriteMethod("writeString", "(ILjava/lang/String;)V");
                if (type.getClassName().equals(BigDecimal.class.getName())) return new WriteMethod("writeBigDecimal", "(ILjava/math/BigDecimal;)V");
                if (type.getClassName().equals(BigInteger.class.getName())) return new WriteMethod("writeBigInteger", "(ILjava/math/BigInteger;)V");
                if (type.getClassName().equals(Binary.class.getName()))     return new WriteMethod("writeBinary", "(ILcom/tangosol/util/Binary;)V");
                if (type.getClassName().equals(Date.class.getName()))       return getDateWriteMethod(field, type);
                if (type.getClassName().equals(Timestamp.class.getName()))  return getDateWriteMethod(field, type);
                if (isCollection(field, type))                              return getCollectionWriteMethod(field, type);
                if (isMap(field, type))                                     return getMapWriteMethod(field, type);
                return new WriteMethod("writeObject", "(ILjava/lang/Object;)V");
        }
    }

    private WriteMethod getDateWriteMethod(FieldNode field, Type type) {
        String name = "writeDateTime";
        String desc = "(I" + type.getDescriptor() + ")V";

        AnnotationNode an = getAnnotation(field, PortableDate.class);
        if (an != null) {
            DateMode mode = (DateMode) getAnnotationAttribute(an, "mode");
            switch (mode) {
                case DATE:
                    name = "writeDate";
                    break;
                case TIME:
                    name = "writeTime";
                    break;
                case DATE_TIME:
                    name = "writeDateTime";
                    break;
            }
            if (mode != DateMode.DATE) {
                boolean includeTimezone = (Boolean) getAnnotationAttribute(an, "includeTimezone");
                if (includeTimezone) {
                    name += "WithZone";
                }
            }
        }

        return new WriteMethod(name, desc);
    }

    private WriteMethod getCollectionWriteMethod(FieldNode field, Type type) {
        Type elementClass = OBJECT_TYPE;

        AnnotationNode an = getAnnotation(field, PortableList.class, PortableSet.class);
        if (an != null) {
            elementClass = (Type) getAnnotationAttribute(an, "elementClass");
        }

        return new CollectionWriteMethod(elementClass);
    }

    private WriteMethod getMapWriteMethod(FieldNode field, Type type) {
        Type keyClass   = OBJECT_TYPE;
        Type valueClass = OBJECT_TYPE;

        AnnotationNode an = getAnnotation(field, PortableMap.class);
        if (an != null) {
            keyClass = (Type) getAnnotationAttribute(an, "keyClass");
            if (!OBJECT_TYPE.equals(keyClass)) {
                valueClass = (Type) getAnnotationAttribute(an, "valueClass");
            }
        }

        return new MapWriteMethod(keyClass, valueClass);
    }

    private WriteMethod getObjectArrayWriteMethod(FieldNode field, Type type) {
        Type elementClass = OBJECT_TYPE;

        AnnotationNode an = getAnnotation(field, PortableArray.class);
        if (an != null) {
            elementClass = (Type) getAnnotationAttribute(an, "elementClass");
        }

        return new ObjectArrayWriteMethod(elementClass);
    }

    private boolean isCollection(FieldNode field, Type type) {
        return COLLECTION_CLASSES.contains(type.getClassName())
                || hasAnnotation(field, PortableList.class)
                || hasAnnotation(field, PortableSet.class);
    }

    private boolean isList(FieldNode field, Type type) {
        return LIST_CLASSES.contains(type.getClassName())
                || hasAnnotation(field, PortableList.class);
    }

    private boolean isSet(FieldNode field, Type type) {
        return SET_CLASSES.contains(type.getClassName())
                || hasAnnotation(field, PortableSet.class);
    }

    private boolean isMap(FieldNode field, Type type) {
        return MAP_CLASSES.contains(type.getClassName())
                || hasAnnotation(field, PortableMap.class);
    }

    private class FieldNodeComparator implements Comparator<FieldNode> {
        @Override
        public int compare(FieldNode f1, FieldNode f2) {
            Integer idx1 = (Integer) getAnnotationAttribute(getAnnotation(f1, PORTABLE_ANNOTATIONS), "order");
            Integer idx2 = (Integer) getAnnotationAttribute(getAnnotation(f2, PORTABLE_ANNOTATIONS), "order");
            int cmp = idx1.compareTo(idx2);

            return cmp == 0 ? f1.name.compareTo(f2.name) : cmp;
        }
    }

    private static class ReadMethod extends Method {
        public ReadMethod(String name, String desc) {
            super(name, desc);
        }

        public void createTemplate(MethodNode mn, FieldNode field, Type type) {}
    }

    private static class WriteMethod extends Method {
        public WriteMethod(String name, String desc) {
            super(name, desc);
        }

        public void pushUniformTypes(MethodNode mn) {}
    }

    private class CollectionReadMethod extends ReadMethod {
        public CollectionReadMethod() {
            this("readCollection", "(ILjava/util/Collection;)Ljava/util/Collection;");
        }

        protected CollectionReadMethod(String name, String desc) {
            super(name, desc);
        }

        @Override
        public void createTemplate(MethodNode mn, FieldNode field, Type type) {
            AnnotationNode an = getAnnotation(field, PortableSet.class, PortableList.class, PortableMap.class, Portable.class);

            Type factory = getFactoryType(an);
            if (factory != null) {
                mn.visitTypeInsn(NEW, factory.getInternalName());
                mn.visitInsn(DUP);
                mn.visitMethodInsn(INVOKESPECIAL, factory.getInternalName(), "<init>", "()V");
                mn.visitMethodInsn(INVOKEVIRTUAL, factory.getInternalName(), "create", "()Ljava/lang/Object;");
            }
            else {
                Type clazz = (Type) getAnnotationAttribute(an, "clazz");
                if (clazz.equals(Type.getType(Object.class))) {
                    clazz = getDefaultClass(field, type);
                }
                mn.visitLdcInsn(clazz);
                mn.visitMethodInsn(INVOKESTATIC, Type.getInternalName(AsmUtils.class), "createInstance", "(Ljava/lang/Class;)Ljava/lang/Object;");
            }
        }

        private Type getDefaultClass(FieldNode fn, Type type) {
            if (isSet(fn, type))  return Type.getType(HashSet.class);
            if (isList(fn, type)) return Type.getType(ArrayList.class);
            if (isMap(fn, type))  return Type.getType(HashMap.class);
            throw new IllegalStateException("Property " + cn.name + "." + fn.name + " must have explicitly defined class or factory");
        }
    }

    private static class CollectionWriteMethod extends WriteMethod {
        private Type elementClass;

        public CollectionWriteMethod(Type elementClass) {
            this("writeCollection", createDescriptor(elementClass), elementClass);
        }

        protected CollectionWriteMethod(String name, String desc, Type elementClass) {
            super(name, desc);
            this.elementClass = elementClass;
        }

        private static String createDescriptor(Type elementClass) {
            String desc = "(ILjava/util/Collection;";
            if (isUniform(elementClass)) {
                desc += "Ljava/lang/Class;";
            }
            return desc + ")V";
        }

        protected static boolean isUniform(Type elementClass) {
            return !OBJECT_TYPE.equals(elementClass);
        }

        @Override
        public void pushUniformTypes(MethodNode mn) {
            if (isUniform(elementClass)) {
                mn.visitLdcInsn(elementClass);
            }
        }
    }

    private class MapReadMethod extends CollectionReadMethod {
        public MapReadMethod() {
            super("readMap", "(ILjava/util/Map;)Ljava/util/Map;");
        }
    }

    private static class MapWriteMethod extends WriteMethod {
        private Type keyClass;
        private Type valueClass;

        public MapWriteMethod(Type keyClass, Type valueClass) {
            super("writeMap", createDescriptor(keyClass, valueClass));
            this.keyClass   = keyClass;
            this.valueClass = valueClass;
        }

        private static String createDescriptor(Type keyClass, Type valueClass) {
            String desc = "(ILjava/util/Map;";
            if (isUniform(keyClass)) {
                desc += "Ljava/lang/Class;";
                if (isUniform(valueClass)) {
                    desc += "Ljava/lang/Class;";
                }
            }
            return desc + ")V";
        }

        protected static boolean isUniform(Type elementClass) {
            return !OBJECT_TYPE.equals(elementClass);
        }

        @Override
        public void pushUniformTypes(MethodNode mn) {
            if (isUniform(keyClass)) {
                mn.visitLdcInsn(keyClass);
                if (isUniform(valueClass)) {
                    mn.visitLdcInsn(valueClass);
                }
            }
        }
    }

    private static class ObjectArrayReadMethod extends ReadMethod {
        public ObjectArrayReadMethod() {
            super("readObjectArray", "(I[Ljava/lang/Object;)[Ljava/lang/Object;");
        }

        @Override
        public void createTemplate(MethodNode mn, FieldNode field, Type type) {
            mn.visitInsn(ICONST_0);
            mn.visitTypeInsn(ANEWARRAY, type.getElementType().getInternalName());
        }
    }

    private static class ObjectArrayWriteMethod extends CollectionWriteMethod {
        private ObjectArrayWriteMethod(Type elementClass) {
            super("writeObjectArray", createDescriptor(elementClass), elementClass);
        }

        private static String createDescriptor(Type elementClass) {
            String desc = "(I[Ljava/lang/Object;";
            if (isUniform(elementClass)) {
                desc += "Ljava/lang/Class;";
            }
            return desc + ")V";
        }
    }

    public static interface Logger {
        void debug(String message);
        void info(String message);
    }

    public static class ConsoleLogger implements Logger {
        @Override
        public void debug(String message) {
            System.out.println("[DEBUG] " + message);
        }

        @Override
        public void info(String message) {
            System.out.println("[INFO] " + message);
        }
    }

    public static class SLF4JLogger implements Logger {
        private org.slf4j.Logger log = LoggerFactory.getLogger(PortableTypeGenerator.class);

        @Override
        public void debug(String message) {
            log.debug(message);
        }

        @Override
        public void info(String message) {
            log.info(message);
        }
    }

    public static class MavenLogger implements Logger {
        private org.apache.maven.plugin.logging.Log log;

        public MavenLogger(Log log) {
            this.log = log;
        }

        @Override
        public void debug(String message) {
            log.debug(message);
        }

        @Override
        public void info(String message) {
            log.info(message);
        }
    }

    public static void instrumentClasses(File classDir) throws IOException {
        if (!classDir.exists()) {
            throw new IllegalArgumentException("Specified path [" + classDir.getAbsolutePath() + "] does not exist");
        }
        if (!classDir.isDirectory()) {
            throw new IllegalArgumentException("Specified path [" + classDir.getAbsolutePath() + "] is not a directory");
        }

        File[] files = classDir.listFiles();
        if (files != null) {
            PofConfig cfg = null;

            for (File file : files) {
                if (file.isDirectory()) {
                    instrumentClasses(file);
                }
                else if (file.getName().endsWith(".class")) {
                    FileInputStream in = new FileInputStream(file);
                    PortableTypeGenerator gen = new PortableTypeGenerator(in);
                    in.close();

                    UserType type = gen.instrumentClass();
                    if (type != null) {
                        if (cfg == null) {
                            cfg = new PofConfig();
                            cfg.setUserTypeList(new UserTypeList());
                            cfg.setDefaultSerializer(new SerializerType("com.seovic.pof.PortableTypeSerializer", null));
                        }
                        cfg.getUserTypeList().getUserTypeOrInclude().add(type);
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    gen.writeClass(out);
                    out.flush();
                    out.close();
                }
            }

            if (cfg != null) {
                File pofConfig = new File(classDir, "pof-config.xml");
                JAXBMarshaller marshaller = new JAXBMarshaller(PofConfig.class, true);
                marshaller.marshal(cfg, new FileOutputStream(pofConfig));
                LOG.info("Created " + pofConfig.getAbsolutePath());
            }
        }
    }

    // ---- command line utility implementation -----------------------------------------------------------------------

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: PortableTypeGenerator <classDir>");
            System.exit(0);
        }

        try {
            instrumentClasses(new File(args[0]));
        }
        catch (Exception e) {
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    // ---- JVM agent implementation ----------------------------------------------------------------------------------

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        instrumentation.addTransformer(new PortableTypeTransformer());
    }

    private static class PortableTypeTransformer
            implements ClassFileTransformer {
        @Override
        public byte[] transform(ClassLoader classLoader, String s, Class<?> aClass, ProtectionDomain protectionDomain, byte[] bytes) throws IllegalClassFormatException {
            try {
                PortableTypeGenerator gen = new PortableTypeGenerator(new ByteArrayInputStream(bytes));
                gen.instrumentClass();
                return gen.getClassBytes();
            }
            catch (IOException e) {
                throw new IllegalClassFormatException(e.getMessage());
            }
        }
    }
}
