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


import com.tangosol.io.DefaultSerializer;
import com.tangosol.io.Serializer;
import com.tangosol.io.pof.EnumPofSerializer;
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import java.lang.reflect.Constructor;
import org.junit.Before;
import org.junit.Test;
import test.Color;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2012.05.27
 */
public class PortableTypeSerializerTest {
    private SimplePofContext portable;
    private SimplePofContext v1;
    private SimplePofContext v2;
    private SimplePofContext v3;
    private ClassLoader v3loader = new PortableTypeLoader();
    private Constructor v3DogCtor;
    private Constructor emptyClassCtor;

    @Before
    public void setup() throws Exception {
        portable = new SimplePofContext();
        portable.registerUserType(1, test.portable.Pet.class, new PortableObjectSerializer(1));
        portable.registerUserType(2, test.portable.Dog.class, new PortableObjectSerializer(2));
        portable.registerUserType(3, test.portable.Animal.class, new PortableObjectSerializer(3));
        portable.registerUserType(5, Color.class, new EnumPofSerializer());

        v1 = new SimplePofContext();
        v1.registerUserType(1, test.v1.Pet.class, new PortableTypeSerializer(1, test.v1.Pet.class));
        v1.registerUserType(2, test.v1.Dog.class, new PortableTypeSerializer(2, test.v1.Dog.class));

        v2 = new SimplePofContext();
        v2.registerUserType(1, test.v2.Pet.class, new PortableTypeSerializer(1, test.v2.Pet.class));
        v2.registerUserType(2, test.v2.Dog.class, new PortableTypeSerializer(2, test.v2.Dog.class));
        v2.registerUserType(3, test.v2.Animal.class, new PortableTypeSerializer(3, test.v2.Animal.class));
        v2.registerUserType(5, Color.class, new EnumPofSerializer());

        v3 = new SimplePofContext();
        Class v3Dog = v3loader.loadClass("test.v3.Dog");
        v3DogCtor = v3Dog.getConstructor(String.class, Integer.TYPE, String.class, Color.class);
        v3.registerUserType(1, v3loader.loadClass("test.v3.Pet"), new PortableTypeSerializer(1, v3loader.loadClass("test.v3.Pet")));
        v3.registerUserType(2, v3Dog, new PortableTypeSerializer(2, v3Dog));
        v3.registerUserType(3, v3loader.loadClass("test.v3.Animal"), new PortableTypeSerializer(3, v3loader.loadClass("test.v3.Animal")));
        v3.registerUserType(5, Color.class, new EnumPofSerializer());
        Class emptyClass =  v3loader.loadClass("test.EmptyClass");
        emptyClassCtor = emptyClass.getConstructor();
        v3.registerUserType(6, emptyClass, new PortableTypeSerializer(6, emptyClass));
    }

    @Test
    public void testRoundTripV1() throws Exception {
        test.v1.Dog dog = new test.v1.Dog("Nadia", "Boxer");
        System.out.println(dog);
        Binary binDog = ExternalizableHelper.toBinary(dog, v1);
        assertEquals(dog, ExternalizableHelper.fromBinary(binDog, v1));
    }

    @Test
    public void testRoundTripV2() throws Exception {
        test.v2.Dog dog = new test.v2.Dog("Nadia", 10, "Boxer", Color.BRINDLE);
        System.out.println(dog);
        Binary binDog = ExternalizableHelper.toBinary(dog, v2);
        assertEquals(dog, ExternalizableHelper.fromBinary(binDog, v2));
    }

    @Test
    public void testRoundTripV3() throws Exception {
        Object dog = v3DogCtor.newInstance("Nadia", 10, "Boxer", Color.BRINDLE);
        System.out.println(dog);
        Binary binDog = ExternalizableHelper.toBinary(dog, v3);
        assertEquals(dog, ExternalizableHelper.fromBinary(binDog, v3));
    }

    @Test
    public void testEvolution() throws Exception {
        test.v2.Dog dogV2 = new test.v2.Dog("Nadia", 10, "Boxer", Color.BRINDLE);
        System.out.println(dogV2);
        Binary binDogV2 = ExternalizableHelper.toBinary(dogV2, v2);

        test.v1.Dog dogV1 = (test.v1.Dog) ExternalizableHelper.fromBinary(binDogV2, v1);
        System.out.println(dogV1);
        Binary binDogV1 = ExternalizableHelper.toBinary(dogV1, v1);

        Object dog = ExternalizableHelper.fromBinary(binDogV1, v2);
        System.out.println(dog);

        assertEquals(dogV2, dog);
    }

    @Test
    public void testRoundTripEmptyClass() throws Exception {
        Object ec = emptyClassCtor.newInstance();
        Binary bin = ExternalizableHelper.toBinary(ec, v3);
        assertNotNull(ExternalizableHelper.fromBinary(bin, v3));
    }

    @Test
    public void testPerformance() throws Exception {
        final int ITERATIONS = 100000;

        // ---- PortableObjectSerializer ------------------------------------

        Object expected = new test.portable.Dog("Nadia", 10, "Boxer", Color.BRINDLE);
        Object actual = null;
        // warmup
        for (int i = 0; i < ITERATIONS; i++) {
            Binary binary = ExternalizableHelper.toBinary(expected, portable);
            actual = ExternalizableHelper.fromBinary(binary, portable);
        }

        // test
        long start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            Binary binary = ExternalizableHelper.toBinary(expected, portable);
            actual = ExternalizableHelper.fromBinary(binary, portable);
        }
        long duration = System.currentTimeMillis() - start;
        System.out.println("PortableObjectSerializer took " + duration + "ms for " + ITERATIONS + " iterations (" + 1.0*duration/ITERATIONS + "ms per object)");
        assertEquals(expected, actual);

        // ---- Java serialization ------------------------------------------

        Serializer java = new DefaultSerializer();
        // warmup
        for (int i = 0; i < ITERATIONS; i++) {
            Binary binary = ExternalizableHelper.toBinary(expected, java);
            actual = ExternalizableHelper.fromBinary(binary, java);
        }

        // test
        start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            Binary binary = ExternalizableHelper.toBinary(expected, java);
            actual = ExternalizableHelper.fromBinary(binary, java);
        }
        duration = System.currentTimeMillis() - start;
        System.out.println("Java serialization took " + duration + "ms for " + ITERATIONS + " iterations (" + 1.0*duration/ITERATIONS + "ms per object)");
        assertEquals(expected, actual);

        // ---- PortableTypeSerializer --------------------------------------

        expected = v3DogCtor.newInstance("Nadia", 10, "Boxer", Color.BRINDLE);
        actual = null;
        // warmup
        for (int i = 0; i < ITERATIONS; i++) {
            Binary binary = ExternalizableHelper.toBinary(expected, v3);
            actual = ExternalizableHelper.fromBinary(binary, v3);
        }

        // test
        start = System.currentTimeMillis();
        for (int i = 0; i < ITERATIONS; i++) {
            Binary binary = ExternalizableHelper.toBinary(expected, v3);
            actual = ExternalizableHelper.fromBinary(binary, v3);
        }
        duration = System.currentTimeMillis() - start;
        System.out.println("PortableTypeSerializer took " + duration + "ms for " + ITERATIONS + " iterations (" + 1.0*duration/ITERATIONS + "ms per object)");
        assertEquals(expected, actual);
    }
}
