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
import java.io.ByteArrayInputStream;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * GroovyExpression tests.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
public class ScriptExpressionTest
        extends AbstractExpressionTest {

    protected Expression createExpression(String expression) {
        if ("name".equals(expression)) expression = "target.name";
        if ("address.city".equals(expression)) {
            expression = "target.address.city";
        }
        if ("name + ' ' + lastName".equals(expression)) {
            expression = "target.name + ' ' + lastName";
        }

        return new ScriptExpression(expression);
    }

    protected String getLanguage() {
        return "Javascript";
    }

    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void testSetExpression() {
        super.testSetExpression();
    }

    @Ignore
    @Override
    public void testPerformance() {
    }

    @Test
    public void testExpressionFromStream() {
        ByteArrayInputStream in = new ByteArrayInputStream("5 + 5".getBytes());
        Expression exp = new ScriptExpression(in);
        assertEquals(10, ((Number) exp.evaluate(null)).intValue());
    }
}