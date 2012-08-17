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

package com.seovic.core.util;


import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * Utility methods for numeric types.
 *
 * @author Aleksandar Seovic  2009.09.29
 */
public abstract class Numbers
    {
    /**
     * Return default value (zero) for the specified numeric type.
     *
     * @param cls  numeric type
     *
     * @return default value (zero) for the specified numeric type
     */
    public static Number getDefaultValue(Class<? extends Number> cls)
        {
        if (cls == null)
            {
            throw new IllegalArgumentException("Class argument cannot be null.");
            }

        return cls.equals(Integer.class)    ? INT_ZERO
             : cls.equals(Long.class)       ? LONG_ZERO
             : cls.equals(Double.class)     ? DOUBLE_ZERO
             : cls.equals(Float.class)      ? FLOAT_ZERO
             : cls.equals(BigInteger.class) ? BIGINTEGER_ZERO
             : cls.equals(BigDecimal.class) ? BIGDECIMAL_ZERO
             : cls.equals(Short.class)      ? SHORT_ZERO
             : cls.equals(Byte.class)       ? BYTE_ZERO
             : null;
        }

    public static Number add(Number m, Number n)
        {
        // TODO: implement Numbers.add
        return null;
        }

    public static Number subtract(Number m, Number n)
        {
        // TODO: implement Numbers.subtract
        return null;
        }

    public static Number multiply(Number m, Number n)
        {
        // TODO: implement Numbers.multiply
        return null;
        }

    public static Number divide(Number m, Number n)
        {
        // TODO: implement Numbers.divide
        return null;
        }

    // ---- constants -------------------------------------------------------

    public static final Integer    INT_ZERO        = 0;
    public static final Long       LONG_ZERO       = 0L;
    public static final Double     DOUBLE_ZERO     = 0.0;
    public static final Float      FLOAT_ZERO      = 0.0f;
    public static final BigInteger BIGINTEGER_ZERO = BigInteger.ZERO;
    public static final BigDecimal BIGDECIMAL_ZERO = new BigDecimal(BIGINTEGER_ZERO);
    public static final Short      SHORT_ZERO      = 0;
    public static final Byte       BYTE_ZERO       = 0;
    }
