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


import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2010.10.11
 */
public class PropertyListTest {
    @Test
    public void testFromString() {
        PropertyList pl = PropertyList.fromString("name,address:(city,state),orders:(date,lineItems:(sku,price),amount)");

        List<PropertySpec> l = pl.getProperties();
        assertEquals(3, l.size());

        assertEquals("name", l.get(0).getName());
        assertNull(l.get(0).getPropertyList());

        assertEquals("address", l.get(1).getName());
        assertNotNull(l.get(1).getPropertyList());

        assertEquals("orders", l.get(2).getName());
        assertNotNull(l.get(2).getPropertyList());

        PropertySpec lineItems = l.get(2).getPropertyList().getProperties().get(1);
        assertEquals("lineItems", lineItems.getName());
        assertNotNull(lineItems.getPropertyList());
    }
}
