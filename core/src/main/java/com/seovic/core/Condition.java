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


import com.tangosol.util.Filter;


/**
 * Condition interface represents a logical condition that can be evaluated to
 * boolean <tt>true</tt> or <tt>false</tt>.
 *
 * @author Aleksandar Seovic  2009.06.17
 */
public interface Condition
        extends Filter {
    // ---- constants -------------------------------------------------------

    /**
     * Condition that always evaluates to <tt>true</tt>.
     */
    Condition TRUE = new Condition() {
        public boolean evaluate(Object target) {
            return true;
        }
    };

    /**
     * Condition that always evaluates to <tt>false</tt>.
     */
    Condition FALSE = new Condition() {
        public boolean evaluate(Object target) {
            return false;
        }
    };


    // ---- interface members -----------------------------------------------

    /**
     * Evaluates condition against the target object.
     *
     * @param target object to evaluate condition for
     *
     * @return <tt>true</tt> if the specified target object satisfies condition,
     *         <tt>false</tt> otherwise
     */
    boolean evaluate(Object target);
}
