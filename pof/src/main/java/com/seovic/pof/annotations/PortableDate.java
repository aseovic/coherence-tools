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


import com.seovic.pof.DateMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PortableDate {
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
     * Serialization mode for date/time value.
     *
     * @return serialization mode for date/time value
     */
    DateMode mode() default DateMode.DATE_TIME;

    /**
     * Flag specifying whether to include timezone information
     * when serializing annotated field.
     *
     * @return whether to include timezone information
     */
    boolean includeTimezone() default false;
}
