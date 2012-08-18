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

package com.seovic.core.objects;


import com.seovic.test.objects.Address;
import com.seovic.test.objects.Person;
import java.util.Date;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2010.10.11
 */
@SuppressWarnings({"deprecation"})
public class DynamicObjectTest {
    @Test
    public void testConstructionFromBean() {
        Date dob = new Date();
        Person p = new Person(1L, "Homer", dob, new Address("111 Main St", "Springfield", "USA"));
        DynamicObject o = new DynamicObject(p);

        assertEquals(1L, o.getLong("id"));
        assertEquals("Homer", o.getString("name"));
        assertEquals(dob, o.getDate("dateOfBirth"));
        assertEquals(p.getAddress(), o.getValue("address"));
    }

    @Test
    public void testConstructionFromBeanWithPropertyList() {
        Person p = new Person(1L, "Homer", null, new Address("111 Main St", "Springfield", "USA"));
        DynamicObject o = new DynamicObject(p, PropertyList.fromString("name,address:(city,country)"));

        assertNull(o.getDate("dob"));
        assertEquals("Homer", o.getString("name"));

        DynamicObject address = (DynamicObject) o.getValue("address");
        assertEquals("Springfield", address.getString("city"));
        assertEquals("USA", address.getString("country"));
    }

    @Test
    public void testUpdate() {
        Person p = new Person(1L, "Homer", null, new Address("111 Main St", "Springfield", "USA"));

        DynamicObject address = new DynamicObject();
        address.setValue("street", "555 Main St");

        DynamicObject o = new DynamicObject();
        o.setValue("id", "5");
        o.setValue("name", "Homer Simpson");
        o.setValue("dateOfBirth", "2010-10-10");
        o.setValue("address", address);

        o.update(p);

        assertEquals(5L, p.getId());
        assertEquals("Homer Simpson", p.getName());
        assertEquals(new Date(110, 9, 10), p.getDateOfBirth());
        assertEquals("555 Main St", p.getAddress().getStreet());
    }
}