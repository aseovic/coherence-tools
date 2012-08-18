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

package com.seovic.pof.annotations;


import com.seovic.core.Factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Portable {
    /**
     * Type version this property was introduced in.
     *
     * @return type version this property was introduced in
     */
    int since() default 0;

    /**
     * Property order.
     *
     * If not specified, it will be determined based on the combination of the type version property was
     * introduced in and the alphabetical order of property names within a type version.
     *
     * @return property order
     */
    int order() default Integer.MAX_VALUE;

    /**
     * Property class.
     *
     * @return property class
     */
    Class clazz() default Object.class;

    /**
     * Factory class for the attribute.
     *
     * This attribute allows you to specify a {@link Factory} implementation that should be used to create
     * property instance during deserialization. It is typically used to better control deserialization of
     * collections and maps.
     *
     * @return factory class for the attribute
     */
    Class<? extends Factory> factory() default Factory.class;
}
