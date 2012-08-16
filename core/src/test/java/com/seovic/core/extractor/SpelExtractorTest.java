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
import com.seovic.core.Extractor;
import com.seovic.core.expression.SpelExpression;


/**
 * Tests for {@link ExpressionExtractor} in combination with {@link
 * SpelExpression}.
 *
 * @author Aleksandar Seovic  2009.09.30
 */
public class SpelExtractorTest
        extends AbstractExtractorTest {
    @Override
    protected Extractor createExtractor(String expression) {
        return new SpelExtractor(expression);
    }

    @Override
    protected String getName() {
        return "SpelExtractor";
    }
}