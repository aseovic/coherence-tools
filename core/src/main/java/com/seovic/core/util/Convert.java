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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Aleksandar Seovic  2010.08.18
 */
public abstract class Convert {
    public static boolean toBoolean(Object o) {
        if (o == null)            return false;
        if (o instanceof Boolean) return (Boolean) o;
        if (o instanceof String)  return Boolean.parseBoolean((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to boolean");
    }

    public static char toChar(Object o) {
        if (o == null)              return Character.MIN_VALUE;
        if (o instanceof Character) return (Character) o;
        if (o instanceof String)    return ((String) o).charAt(0);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to char");
    }

    public static byte toByte(Object o) {
        if (o == null)            return 0;
        if (o instanceof Byte)    return (Byte) o;
        if (o instanceof Short)   return ((Short) o).byteValue();
        if (o instanceof Integer) return ((Integer) o).byteValue();
        if (o instanceof Long)    return ((Long) o).byteValue();
        if (o instanceof String)  return Byte.parseByte((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to byte");
    }

    public static short toShort(Object o) {
        if (o == null)            return 0;
        if (o instanceof Byte)    return ((Byte) o).shortValue();
        if (o instanceof Short)   return (Short) o;
        if (o instanceof Integer) return ((Integer) o).shortValue();
        if (o instanceof Long)    return ((Long) o).shortValue();
        if (o instanceof String)  return Short.parseShort((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to short");
    }

    public static int toInt(Object o) {
        if (o == null)            return 0;
        if (o instanceof Byte)    return ((Byte) o).intValue();
        if (o instanceof Short)   return ((Short) o).intValue();
        if (o instanceof Integer) return (Integer) o;
        if (o instanceof Long)    return ((Long) o).intValue();
        if (o instanceof String)  return Integer.parseInt((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to integer");
    }

    public static long toLong(Object o) {
        if (o == null)            return 0L;
        if (o instanceof Byte)    return ((Byte) o).longValue();
        if (o instanceof Short)   return ((Short) o).longValue();
        if (o instanceof Integer) return ((Integer) o).longValue();
        if (o instanceof Long)    return (Long) o;
        if (o instanceof String)  return Long.parseLong((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to long");
    }

    public static float toFloat(Object o) {
        if (o == null)            return 0.0f;
        if (o instanceof Byte)    return ((Byte) o).floatValue();
        if (o instanceof Short)   return ((Short) o).floatValue();
        if (o instanceof Integer) return ((Integer) o).floatValue();
        if (o instanceof Long)    return ((Long) o).floatValue();
        if (o instanceof Float)   return (Float) o;
        if (o instanceof Double)  return ((Double) o).floatValue();
        if (o instanceof String)  return Float.parseFloat((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to float");
    }

    public static double toDouble(Object o) {
        if (o == null)            return 0.0d;
        if (o instanceof Byte)    return ((Byte) o).doubleValue();
        if (o instanceof Short)   return ((Short) o).doubleValue();
        if (o instanceof Integer) return ((Integer) o).doubleValue();
        if (o instanceof Long)    return ((Long) o).doubleValue();
        if (o instanceof Float)   return ((Float) o).doubleValue();
        if (o instanceof Double)  return (Double) o;
        if (o instanceof String)  return Double.parseDouble((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to double");
    }

    public static BigDecimal toBigDecimal(Object o) {
        if (o == null)            return BigDecimal.ZERO;
        if (o instanceof Byte)    return new BigDecimal(((Byte) o).intValue());
        if (o instanceof Short)   return new BigDecimal(((Short) o).intValue());
        if (o instanceof Integer) return new BigDecimal((Integer) o);
        if (o instanceof Long)    return new BigDecimal((Long) o);
        if (o instanceof Float)   return new BigDecimal((Float) o);
        if (o instanceof Double)  return new BigDecimal((Double) o);
        if (o instanceof String)  return new BigDecimal((String) o);
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to BigDecimal");
    }

    public static Date toDate(Object o) {
        if (o == null)           return null;
        if (o instanceof Date)   return (Date) o;
        if (o instanceof String) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse((String) o);
            }
            catch (ParseException e) {
                return null;
            }
        }
        throw new IllegalArgumentException("Can't convert " + o.getClass().getSimpleName() + " to Date");
    }
}
