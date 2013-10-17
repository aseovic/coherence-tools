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

package com.seovic.scripting.groovy;


import com.seovic.core.Extractor;
import com.seovic.core.extractor.AbstractExtractorTest;
import com.tangosol.io.pof.PofContext;
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;


/**
 * Tests for {@link GroovyExtractor} in combination with {@link GroovyExpression}.
 *
 * @author Aleksandar Seovic  2009.09.18
 */
public class GroovyExtractorTest
        extends AbstractExtractorTest
    {
    @Override
    protected Extractor createExtractor(String expression) {
        return new GroovyExtractor(translateExpression(expression));
    }

    protected void registerUserTypes(SimplePofContext ctx, int index)
        {
        ctx.registerUserType(index, GroovyExpression.class, new PortableObjectSerializer(index));
        }

    @Override
    protected String getName() {
        return "GroovyExtractor";
    }

    private String translateExpression(String expression) {
        if ("name".equals(expression)) expression = "target.name";
        if ("address.city".equals(expression)) {
            expression = "target.address.city";
        }

        return expression;
    }
}