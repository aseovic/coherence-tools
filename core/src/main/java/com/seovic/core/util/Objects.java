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

package com.seovic.core.util;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Stack;

//import static com.seovic.agent.InstrumentationHolder.*;


/**
 * Miscellaneous object utilities.
 * <p/>
 * The {@link #sizeOf(Object)}, {@link #deepSizeOf(Object)} and related methods were originally published by
 * Dr. Heinz M. Kabutz in <a href="http://www.javaspecialists.eu/archive/Issue142.html">The Java
 * Specialists' Newsletter, Issue #142</a>, which was itself based on the
 * <a href="http://jroller.com/maxim/entry/again_about_determining_size_of">implementation by
 * Maxim Zakharenkov</a>, and are included with the authors' permission.
 *
 * @author Dr. Heinz M. Kabutz
 * @author Maxim Zakharenkov
 * @author Aleksandar Seovic  2012.09.20
 */
@SuppressWarnings("unchecked")
public abstract class Objects {
//    /**
//     * Returns shallow object size.
//     *
//     * @param obj  object to calculate size for
//     */
//    public static int sizeOf(Object obj) {
//        if (instrumentation == null) {
//            throw new IllegalStateException(
//                    "Instrumentation environment not initialised.");
//        }
//        if (isSharedFlyweight(obj)) {
//            return 0;
//        }
//        return (int) instrumentation.getObjectSize(obj);
//    }
//
//    /**
//     * Returns deep size of object, recursively iterating over its fields and superclasses.
//     *
//     * @param obj  object to calculate size for
//     */
//    public static int deepSizeOf(Object obj) {
//        Map visited = new IdentityHashMap();
//        Stack stack = new Stack();
//        stack.push(obj);
//
//        int result = 0;
//        do {
//            result += internalSizeOf(stack.pop(), stack, visited);
//        }
//        while (!stack.isEmpty());
//
//        return result;
//    }
//
//    /**
//     * Returns true if this is a well-known shared flyweight. For example, interned Strings, Booleans and
//     * Number objects
//     *
//     * @param obj  object to check
//     */
//    @SuppressWarnings("BoxingBoxedValue")
//    private static boolean isSharedFlyweight(Object obj) {
//        // optimization - all of our flyweights are Comparable
//        if (obj instanceof Comparable) {
//            if (obj instanceof Enum) {
//                return true;
//            }
//            else if (obj instanceof String) {
//                return (obj == ((String) obj).intern());
//            }
//            else if (obj instanceof Boolean) {
//                return (obj == Boolean.TRUE || obj == Boolean.FALSE);
//            }
//            else if (obj instanceof Integer) {
//                return (obj == Integer.valueOf((Integer) obj));
//            }
//            else if (obj instanceof Short) {
//                return (obj == Short.valueOf((Short) obj));
//            }
//            else if (obj instanceof Byte) {
//                return (obj == Byte.valueOf((Byte) obj));
//            }
//            else if (obj instanceof Long) {
//                return (obj == Long.valueOf((Long) obj));
//            }
//            else if (obj instanceof Character) {
//                return (obj == Character.valueOf((Character) obj));
//            }
//        }
//        return false;
//    }
//
//    private static boolean skipObject(Object obj, Map visited) {
//        return obj == null
//               || visited.containsKey(obj)
//               || isSharedFlyweight(obj);
//    }
//
//    private static int internalSizeOf(Object obj, Stack stack, Map visited) {
//        if (skipObject(obj, visited)) {
//            return 0;
//        }
//
//        Class clazz = obj.getClass();
//        if (clazz.isArray()) {
//            addArrayElementsToStack(clazz, obj, stack);
//        }
//        else {
//            // add all non-primitive fields to the stack
//            while (clazz != null) {
//                Field[] fields = clazz.getDeclaredFields();
//                for (Field field : fields) {
//                    if (!Modifier.isStatic(field.getModifiers())
//                        && !field.getType().isPrimitive()) {
//                        field.setAccessible(true);
//                        try {
//                            stack.add(field.get(obj));
//                        }
//                        catch (IllegalAccessException ex) {
//                            throw new RuntimeException(ex);
//                        }
//                    }
//                }
//                clazz = clazz.getSuperclass();
//            }
//        }
//        visited.put(obj, null);
//        return sizeOf(obj);
//    }
//
//    private static void addArrayElementsToStack(Class clazz, Object obj, Stack stack) {
//        if (!clazz.getComponentType().isPrimitive()) {
//            int length = Array.getLength(obj);
//            for (int i = 0; i < length; i++) {
//                stack.add(Array.get(obj, i));
//            }
//        }
//    }
}
