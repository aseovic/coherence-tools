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
import com.seovic.core.expression.SpelExpression;


/**
 * An implementation of {@link Extractor} that extracts value from a target
 * object using {@link SpelExpression}.
 *
 * @author Aleksandar Seovic  2009.11.07
 */
public class SpelExtractor<T>
        extends ExpressionExtractor<T> {

    private static final long serialVersionUID = 2820613492742449783L;

    // ---- constructors ----------------------------------------------------

    /**
     * Deserialization constructor (for internal use only).
     */
    public SpelExtractor() {
    }

    /**
     * Construct an <tt>SpelExtractor</tt> instance.
     *
     * @param expression the expression to use
     */
    public SpelExtractor(String expression) {
        super(new SpelExpression<T>(expression));
    }
}