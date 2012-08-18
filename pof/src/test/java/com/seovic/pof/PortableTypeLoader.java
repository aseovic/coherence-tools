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


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import test.Color;
import test.DateTypes;


/**
* @author Aleksandar Seovic  2012.06.04
*/
class PortableTypeLoader
        extends ClassLoader {
    private Map<String, Class> loadedClasses = new HashMap<String, Class>();

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        if (loadedClasses.containsKey(className)) {
            return loadedClasses.get(className);
        }
        try {
            if (className.startsWith("test.")
                    && !className.equals(Color.class.getName())
                    && !className.equals(DateTypes.class.getName())) {
                InputStream in = new FileInputStream("target/test-classes/" + className.replace('.', '/') + ".class");
                PortableTypeGenerator gen = new PortableTypeGenerator(in);
                gen.instrumentClass();
                byte[] clsBytes = gen.getClassBytes();
                Class<?> clazz = defineClass(className, clsBytes, 0, clsBytes.length);
                loadedClasses.put(className, clazz);

                return clazz;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return super.loadClass(className);
    }
}
