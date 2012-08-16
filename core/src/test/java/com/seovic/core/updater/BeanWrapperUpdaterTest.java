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

package com.seovic.core.updater;


import com.seovic.test.objects.Address;
import com.seovic.test.objects.Person;
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for {BeanWrapperPropertySetter}.
 *
 * @author ic  2009.06.16
 */
public class BeanWrapperUpdaterTest {
    @Test(expected = RuntimeException.class)
    public void testWithBadPropertyName() {
        BeanWrapperUpdater updater = new BeanWrapperUpdater("bad");
        Person person = new Person();
        updater.update(person, "value");
    }

    @Test
    public void testWithStringPropertyType() {
        BeanWrapperUpdater updater = new BeanWrapperUpdater("name");
        Person person = new Person();
        assertNull(person.getName());

        updater.update(person, "Ivan");
        assertEquals("Ivan", person.getName());
    }

    @Test
    public void testWithPrimitivePropertyType() {
        BeanWrapperUpdater updater = new BeanWrapperUpdater("id");
        Person person = new Person();
        assertEquals(0L, person.getId());

        updater.update(person, 2504L);
        assertEquals(2504L, person.getId());
    }

    @Test
    public void testWithDateType() {
        BeanWrapperUpdater updater = new BeanWrapperUpdater("dateOfBirth");
        Person person = new Person();
        assertEquals(null, person.getDateOfBirth());

        updater.update(person, "1974-08-24");
        assertEquals(74, person.getDateOfBirth().getYear());
        assertEquals(7, person.getDateOfBirth().getMonth());
        assertEquals(24, person.getDateOfBirth().getDate());
    }

    @Test
    public void testWithComplexPropertyType() {
        BeanWrapperUpdater updater = new BeanWrapperUpdater("address");
        Person person = new Person();
        assertNull(person.getAddress());

        Address merced = new Address("Merced", "Santiago", "Chile");
        updater.update(person, merced);
        assertEquals(merced, person.getAddress());

        updater = new BeanWrapperUpdater("address.street");
        updater.update(person, "Av Bernardo O'Higgins");
        assertEquals("Av Bernardo O'Higgins", person.getAddress().getStreet());
    }

    @Test
    public void testSerialization() {
        SimplePofContext ctx = new SimplePofContext();
        ctx.registerUserType(1, BeanWrapperUpdater.class, new PortableObjectSerializer(1));

        BeanWrapperUpdater expected = new BeanWrapperUpdater("name");
        Binary bin = ExternalizableHelper.toBinary(expected, ctx);
        BeanWrapperUpdater actual = (BeanWrapperUpdater) ExternalizableHelper.fromBinary(bin, ctx);

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
        assertEquals(expected.toString(), actual.toString());
    }
}
