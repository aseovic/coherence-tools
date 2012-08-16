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

import com.tangosol.util.Base;
import com.tangosol.util.ValueExtractor;
import com.tangosol.util.extractor.ChainedExtractor;


/**
 * Tests for {@link PropertyExtractor}.
 *
 * @author ic  2009.06.16
 */
public class PropertyExtractorTest
        extends AbstractExtractorTest {
    protected Extractor createExtractor(final String expression) {
        return expression.indexOf(".") < 0
               ? new PropertyExtractor(expression)
               : createExtractor(createChainedExtractor(expression));
    }

    private ValueExtractor createChainedExtractor(String expression) {
        String[] parts = Base.parseDelimitedString(expression, '.');
        ValueExtractor[] aVE = new ValueExtractor[parts.length];

        for (int i = 0; i < parts.length; i++) {
            aVE[i] = new PropertyExtractor(parts[i]);
        }

        return new ChainedExtractor(aVE);
    }

    protected String getName() {
        return "PropertyExtractor";
    }
}