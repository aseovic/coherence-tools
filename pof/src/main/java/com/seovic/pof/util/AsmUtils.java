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

package com.seovic.pof.util;


import com.tangosol.coherence.asm.ClassReader;
import com.tangosol.coherence.asm.tree.AnnotationNode;
import com.tangosol.coherence.asm.tree.ClassNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Aleksandar Seovic  2012.06.20
 */
@SuppressWarnings("unchecked")
public class AsmUtils {
    public static Set<String> getVisibleAnnotations(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassNode cn = new ClassNode();
        reader.accept(cn, ClassReader.SKIP_FRAMES);

        HashSet<String> annotations = new HashSet<String>();
        if (cn.visibleAnnotations != null ) {
            for (AnnotationNode an : (List<AnnotationNode>) cn.visibleAnnotations) {
                annotations.add(an.desc);
            }
        }
        return annotations;
    }

    public static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
