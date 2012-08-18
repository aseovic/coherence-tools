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
import com.seovic.pof.annotations.internal.PofIndex;
import com.tangosol.io.pof.reflect.PofNavigator;
import com.tangosol.io.pof.reflect.SimplePofPath;
import com.tangosol.util.Base;
import com.tangosol.util.extractor.PofExtractor;
import com.tangosol.util.extractor.PofUpdater;
import java.lang.reflect.Field;


/**
 * @author Aleksandar Seovic  2012.06.06
 */
@SuppressWarnings("unchecked")
public abstract class PofUtil {
    public static PofNavigator getPofNavigator(Class clazz, String fieldPath) {
        return getNavigatorAndType(clazz, fieldPath).navigator;
    }

    public static PofExtractor getPofExtractor(Class clazz, String fieldPath) {
        NavigatorAndType nt = getNavigatorAndType(clazz, fieldPath);
        return new PofExtractor(nt.type, nt.navigator);
    }
    
    public static PofUpdater getPofUpdater(Class clazz, String fieldPath) {
        NavigatorAndType nt = getNavigatorAndType(clazz, fieldPath);
        return new PofUpdater(nt.navigator);
    }

    private static Field findField(Class clazz, String name) {
        while (clazz.isAnnotationPresent(PortableType.class)) {
            try {
                return clazz.getDeclaredField(name);
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    private static NavigatorAndType getNavigatorAndType(Class clazz, String fieldPath) {
        String[] fieldNames = Base.parseDelimitedString(fieldPath, '.');
        int[] indexes = new int[fieldNames.length * 2];
        int n = 0;

        for (String fieldName : fieldNames) {
            Field field = findField(clazz, fieldName);
            if (field == null) {
                throw new IllegalArgumentException("Class [" + clazz.getName() + "] is not portable type, or the field ["
                        + fieldName + "] does not exist in its hierarchy");
            }

            PofIndex index = field.getAnnotation(PofIndex.class);
            if (index == null) {
                throw new IllegalArgumentException("Field [" + fieldName + "] is not a portable field (@Portable is missing) or the class hasn't been instrumented");
            }

            Class declaringClass = field.getDeclaringClass();
            PortableType type = (PortableType) declaringClass.getAnnotation(PortableType.class);
            indexes[n++] = type.id();
            indexes[n++] = index.value();

            clazz = field.getType();
        }
        return new NavigatorAndType(new SimplePofPath(indexes), clazz);
    }

    private static class NavigatorAndType {
        private PofNavigator navigator;
        private Class type;

        private NavigatorAndType(PofNavigator navigator, Class type) {
            this.navigator = navigator;
            this.type = type;
        }
    }
}
