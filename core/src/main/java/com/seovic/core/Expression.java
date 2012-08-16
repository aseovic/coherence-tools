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


import java.util.Map;


/**
 * Expression interface represents an expression that can be evaluated against
 * the target object.
 *
 * @author Aleksandar Seovic  2009.09.20
 */
public interface Expression<T> {
    /**
     * Evaluates expression against the target object.
     *
     * @param target object to evaluate expression against
     *
     * @return expression result
     */
    T evaluate(Object target);

    /**
     * Evaluates expression against the target object.
     *
     * @param target    object to evaluate expression against
     * @param variables variables to use during evaluation
     *
     * @return expression result
     */
    T evaluate(Object target, Map<String, Object> variables);

    /**
     * Evaluates expression against the target object and sets the last element
     * returned to a specified value.
     *
     * @param target object to evaluate expression against
     * @param value  value to set last element of an expression to
     */
    void evaluateAndSet(Object target, Object value);
}
