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


import com.seovic.core.Expression;
import com.seovic.core.expression.MvelExpression;

import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for {@link ExpressionExtractor} in combination with {@link
 * MvelExpression}.
 *
 * @author Aleksandar Seovic  2009.09.18
 */
@SuppressWarnings("unchecked")
public class LiteralExtractorTest {
    @Test
    public void testLiteralExtractor() {
        assertEquals("blah", new LiteralExtractor("blah").extract(null));
        assertEquals(5, new LiteralExtractor(5).extract(null));
    }

    @Test
    public void testSerialization() {
        Object expected = new LiteralExtractor("blah");

        SimplePofContext ctx = new SimplePofContext();
        ctx.registerUserType(1, expected.getClass(), new PortableObjectSerializer(1));

        Binary bin = ExternalizableHelper.toBinary(expected, ctx);
        Object actual = ExternalizableHelper.fromBinary(bin, ctx);

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
        assertEquals(expected.toString(), actual.toString());
    }
}