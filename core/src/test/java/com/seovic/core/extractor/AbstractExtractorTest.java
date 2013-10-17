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

package com.seovic.core.extractor;


import com.seovic.core.Extractor;
import com.seovic.core.expression.MvelExpression;
import com.seovic.core.expression.OgnlExpression;
import com.seovic.core.expression.ScriptExpression;
import com.seovic.core.expression.SpelExpression;
import com.seovic.test.objects.Address;
import com.seovic.test.objects.Person;
import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PofReader;
import com.tangosol.io.pof.PofWriter;
import com.tangosol.io.pof.PortableObject;
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;
import com.tangosol.util.extractor.ReflectionExtractor;
import java.io.IOException;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Common extractor tests.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
@SuppressWarnings({"unchecked", "RedundantCast"})
public abstract class AbstractExtractorTest {
    protected abstract Extractor createExtractor(String expression);

    protected abstract String getName();
    protected void registerUserTypes(SimplePofContext ctx, int startIndex) {}

    @Test(expected = RuntimeException.class)
    public void testWithBadProperty() {
        Person person = new Person(1L, "Homer");
        Extractor ext = createExtractor("sjdhfgw");
        ext.extract(person);
    }

    @Test
    public void testNullTargetExtraction() {
        Extractor ext = createExtractor("name");
        assertNull(ext.extract(null));
    }

    @Test
    public void testSimplePropertyExtraction() {
        Person person = createTestTarget();
        Extractor ext = createExtractor("name");
        assertEquals("Homer", ext.extract(person));
    }

    @Test
    public void testNestedPropertyExtraction() {
        Person person = createTestTarget();
        Extractor ext = createExtractor("address.city");
        assertEquals("Springfield", ext.extract(person));
    }

    @Test
    public void testPerformance() {
        Person person = new Person(1L, "Homer");
        Extractor ext = createExtractor("name");

        final int COUNT = 1000000;

        // warm up
        for (int i = 0; i < 1000; i++) {
            ext.extract(person);
        }

        // test
        long start = System.nanoTime();
        for (int i = 0; i < COUNT; i++) {
            ext.extract(person);
        }
        long duration = System.nanoTime() - start;

        System.out.println(getName() + ": " + duration / 1000000 + " ms");
    }

    @Test
    public void testSerialization() {
        Extractor expected = createExtractor("name");

        SimplePofContext ctx = new SimplePofContext();
        ctx.registerUserType(1, expected.getClass(), new PortableObjectSerializer(1));
        ctx.registerUserType(3, MvelExpression.class, new PortableObjectSerializer(3));
        ctx.registerUserType(4, OgnlExpression.class, new PortableObjectSerializer(4));
        ctx.registerUserType(5, ScriptExpression.class, new PortableObjectSerializer(5));
        ctx.registerUserType(6, SpelExpression.class, new PortableObjectSerializer(6));
        ctx.registerUserType(7, ExtractorAdapter.class, new PortableObjectSerializer(7));
        ctx.registerUserType(8, ReflectionExtractor.class, new PortableObjectSerializer(8));
        ctx.registerUserType(9, ChainedExtractor.class, new PortableObjectSerializer(9));
        registerUserTypes(ctx, 10);

        Binary bin = ExternalizableHelper.toBinary(expected, ctx);
        Extractor actual = (Extractor) ExternalizableHelper.fromBinary(bin, ctx);

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void testEquals() {
        Extractor ex = createExtractor("name");

        assertTrue(ex.equals(ex));
        assertFalse(ex.equals(null));
        assertFalse(ex.equals("blah"));
    }

    // ---- helper methods --------------------------------------------------

    protected Person createTestTarget() {
        return new Person(1L, "Homer", null,
                          new Address("111 Main St", "Springfield", "USA"));
    }

    protected Extractor createExtractor(final ValueExtractor extractor) {
        return new ExtractorAdapter(extractor);
    }

    public static class ExtractorAdapter implements Extractor, PortableObject {
        private ValueExtractor extractor;

        public ExtractorAdapter() {
        }

        public ExtractorAdapter(ValueExtractor extractor) {
            this.extractor = extractor;
        }

        @Override
        public Object extract(Object target) {
            return extractor.extract(target);
        }

        @Override
        public void readExternal(PofReader in)
                throws IOException {
            extractor = (ValueExtractor) in.readObject(0);
        }

        @Override
        public void writeExternal(PofWriter out)
                throws IOException {
            out.writeObject(0, extractor);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ExtractorAdapter adapter = (ExtractorAdapter) o;

            return extractor.equals(adapter.extractor);
        }

        @Override
        public int hashCode() {
            return extractor.hashCode();
        }

        @Override
        public String toString() {
            return "ExtractorAdapter{" +
                   "extractor=" + extractor +
                   '}';
        }
    }
}