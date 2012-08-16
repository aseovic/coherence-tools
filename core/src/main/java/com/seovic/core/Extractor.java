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

package com.seovic.core;


import com.tangosol.util.ValueExtractor;

import java.io.Serializable;


/**
 * Extractor is used to extract a value from a target object.
 * <p/>
 * Typically a derived value will be a single property of a target object, but
 * it could also be a combination of multiple properties or a result of an
 * expression or a script executed against the target object.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
public interface Extractor<T>
        extends ValueExtractor, Serializable {
    // ---- constants -------------------------------------------------------

    /**
     * Extractor that returns the target object itself.
     */
    Extractor IDENTITY = new Extractor() {
        public Object extract(Object target) {
            return target;
        }
    };

    // ---- interface members -----------------------------------------------

    /**
     * Extract a value from a target object.
     *
     * @param target object to extract the value from
     *
     * @return extracted value
     */
    T extract(Object target);
}
