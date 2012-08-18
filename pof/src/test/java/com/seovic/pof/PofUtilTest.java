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
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;
import com.tangosol.util.ExternalizableHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;
import test.Color;

import static org.junit.Assert.*;


public class PofUtilTest {
    private SimplePofContext ctx;
    private ClassLoader loader = new PortableTypeLoader();

    @Before
    public void setup() throws Exception {
        ctx = new SimplePofContext();
        ctx.registerUserType( 1, loader.loadClass("test.v3.Pet"), new PortableTypeSerializer(1, loader.loadClass("test.v3.Pet")));
        ctx.registerUserType( 2, loader.loadClass("test.v3.Dog"), new PortableTypeSerializer(2, loader.loadClass("test.v3.Dog")));
        ctx.registerUserType( 3, loader.loadClass("test.v3.Animal"), new PortableTypeSerializer(3, loader.loadClass("test.v3.Animal")));
        ctx.registerUserType( 4, loader.loadClass("test.v3.Zoo"), new PortableTypeSerializer(4, loader.loadClass("test.v3.Zoo")));
        ctx.registerUserType( 5, loader.loadClass("test.Color"), new EnumPofSerializer());
        ctx.registerUserType(10, loader.loadClass("test.v3.Address"), new PortableTypeSerializer(10, loader.loadClass("test.v3.Address")));
        ctx.registerUserType(11, loader.loadClass("test.Person"), new PortableObjectSerializer(11));
    }

    @Test
    public void testSimplePath() throws Exception {
        Class dogClass = loader.loadClass("test.v3.Dog");
        Constructor dogCtor = dogClass.getConstructor(String.class, Integer.TYPE, String.class, Color.class);

        Object dog = dogCtor.newInstance("Nadia", 10, "Boxer", Color.BRINDLE);
        Binary binDog = ExternalizableHelper.toBinary(dog, ctx);
        BinaryEntry entry = new TestBinaryEntry(null, binDog, ctx);
        assertEquals("Nadia", PofUtil.getPofExtractor(dogClass, "name").extractFromEntry(entry));
        assertEquals(10, PofUtil.getPofExtractor(dogClass, "age").extractFromEntry(entry));

        PofUtil.getPofUpdater(dogClass, "age").updateEntry(entry, 9);
        Object dog2 = ExternalizableHelper.fromBinary(entry.getBinaryValue(), ctx);
        Method getAge = loader.loadClass("test.v3.Pet").getDeclaredMethod("getAge");
        assertEquals(9, getAge.invoke(dog2));
    }

    @Test
    public void testComplexPath() throws Exception {
        Class addressClass = loader.loadClass("test.v3.Address");
        Constructor addressCtor = addressClass.getConstructor(String.class, String.class, String.class);
        Object address = addressCtor.newInstance("123 Main St", "Portland", "Oregon");

        Class zooClass = loader.loadClass("test.v3.Zoo");
        Constructor zooCtor = zooClass.getConstructor(addressClass);
        Object zoo = zooCtor.newInstance(address);

        Binary binZoo = ExternalizableHelper.toBinary(zoo, ctx);
        BinaryEntry entry = new TestBinaryEntry(null, binZoo, ctx);
        assertEquals("Portland", PofUtil.getPofExtractor(zooClass, "address.city").extractFromEntry(entry));
        assertEquals("Oregon", PofUtil.getPofExtractor(zooClass, "address.state").extractFromEntry(entry));

        PofUtil.getPofUpdater(zooClass, "address.state").updateEntry(entry, "OR");
        Object zoo2 = ExternalizableHelper.fromBinary(entry.getBinaryValue(), ctx);
        Method getAddress = zooClass.getDeclaredMethod("getAddress");
        Method getState = addressClass.getDeclaredMethod("getState");
        assertEquals("OR", getState.invoke(getAddress.invoke(zoo2)));
    }

}
