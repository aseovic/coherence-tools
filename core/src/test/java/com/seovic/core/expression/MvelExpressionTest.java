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

package com.seovic.core.expression;


import com.seovic.core.Expression;
import com.seovic.core.objects.DynamicObject;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * MvelExpression tests.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
public class MvelExpressionTest
        extends AbstractExpressionTest {

    protected Expression createExpression(String expression) {
        return new MvelExpression(expression);
    }

    protected String getLanguage() {
        return "MVEL";
    }

    @SuppressWarnings("unchecked")
    @Ignore
    @Test
    public void testDynamicObjectEvaluation() {
        DynamicObject o = new DynamicObject();
        o.setInt("a", 1);

        Expression<Boolean> exp1 = createExpression("a == 1");
        Expression<Boolean> exp2 = createExpression("a == 0");

        assertTrue(exp1.evaluate(o));
        assertFalse(exp2.evaluate(o));
    }
}