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

package com.seovic.pof;


import com.tangosol.io.pof.EnumPofSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.io.pof.reflect.PofValue;
import com.tangosol.io.pof.reflect.PofValueParser;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.ExternalizableHelper;
import java.lang.reflect.Constructor;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import test.Color;
import test.DateTypes;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2012.05.27
 */
public class PortableTypeGeneratorTest {
    private SimplePofContext ctx;
    private ClassLoader loader = new PortableTypeLoader();
    private Constructor allTypesCtor;
    private Constructor dogCtor;

    @Before
    public void setup() throws Exception {
        Class allTypes = loader.loadClass("test.AllTypes");
        allTypesCtor = allTypes.getConstructor();

        Class dog = loader.loadClass("test.v3.Dog");
        dogCtor = dog.getConstructor(String.class, Integer.TYPE, String.class, Color.class);

        ctx = new SimplePofContext();
        ctx.registerUserType(1, loader.loadClass("test.v3.Pet"), new PortableTypeSerializer(1, loader.loadClass("test.v3.Pet")));
        ctx.registerUserType(2, dog, new PortableTypeSerializer(2, dog));
        ctx.registerUserType(3, loader.loadClass("test.v3.Animal"), new PortableTypeSerializer(3, loader.loadClass("test.v3.Animal")));
        ctx.registerUserType(5, Color.class, new EnumPofSerializer());
        ctx.registerUserType(10, allTypes, new PortableTypeSerializer(10, allTypes));
    }

    @Test
    public void testAllTypesRoundTrip() throws Exception {
        DateTypes expected = (DateTypes) allTypesCtor.newInstance();
        Binary binObj = ExternalizableHelper.toBinary(expected, ctx);
        DateTypes actual = (DateTypes) ExternalizableHelper.fromBinary(binObj, ctx);
        System.out.println("Expected: " + expected);
        System.out.println("Actual:   " + actual);
        assertEquals(expected, actual);
        assertDateEquals(expected.getDate(), actual.getDate());
        assertTimeEquals(expected.getTime(), actual.getTime());
        assertTimeEquals(expected.getTimeWithZone(), actual.getTimeWithZone());
    }

    @Test
    public void testPofNavigator() throws Exception {
        EvolvableObject dog = (EvolvableObject) dogCtor.newInstance("Nadia", 10, "Boxer", Color.BRINDLE);
        System.out.println(dog);
        Binary binDog = ExternalizableHelper.toBinary(dog, ctx);
        PofValue pofDog = PofValueParser.parse(binDog, ctx);

        assertEquals("Nadia", dog.getPofNavigator("name").navigate(pofDog).getString());
        assertEquals("Boxer", dog.getPofNavigator("breed").navigate(pofDog).getString());
        assertEquals(10, dog.getPofNavigator("age").navigate(pofDog).getInt());
        assertEquals(Color.BRINDLE, dog.getPofNavigator("color").navigate(pofDog).getValue());
    }

    @Test
    public void testPofExtractor() throws Exception {
        EvolvableObject dog = (EvolvableObject) dogCtor.newInstance("Nadia", 10, "Boxer", Color.BRINDLE);
        System.out.println(dog);
        Binary binDog = ExternalizableHelper.toBinary(dog, ctx);
        BinaryEntry binEntry = new TestBinaryEntry(null, binDog, ctx);

        assertEquals("Nadia", dog.getPofExtractor("name").extractFromEntry(binEntry));
        assertEquals("Boxer", dog.getPofExtractor("breed").extractFromEntry(binEntry));
        assertEquals(10, dog.getPofExtractor("age").extractFromEntry(binEntry));
        assertEquals(Color.BRINDLE, dog.getPofExtractor("color").extractFromEntry(binEntry));
    }

    private static void assertDateEquals(Date expected, Date actual) {
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getMonth(), actual.getMonth());
        assertEquals(expected.getDate(), actual.getDate());
    }

    private static void assertTimeEquals(Date expected, Date actual) {
        assertEquals(expected.getHours(), actual.getHours());
        assertEquals(expected.getMinutes(), actual.getMinutes());
        assertEquals(expected.getSeconds(), actual.getSeconds());
        assertEquals(expected.getTime() % 1000, actual.getTime() % 1000);
        assertEquals(expected.getTimezoneOffset(), actual.getTimezoneOffset());
    }

}
