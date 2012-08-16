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
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for {@link MapUpdater}.
 *
 * @author ic  2009.06.16
 */
public class MapUpdaterTest {
    @Test
    public void testWithNullProperty() {
        MapUpdater updater = new MapUpdater(null);
        Map<String, Object> target = new HashMap<String, Object>();
        updater.update(target, "KEYED_BY_NULL");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNullTarget() {
        MapUpdater updater = new MapUpdater("prop");
        updater.update(null, "PROP_VALUE");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNonMapTarget() {
        MapUpdater updater = new MapUpdater("prop");
        updater.update("bad target", "PROP_VALUE");
    }

    @Test
    public void testWithNonNullProperty() {
        MapUpdater updater = new MapUpdater("address");
        Address merced = new Address("Merced", "Santiago", "Chile");
        Map<String, Object> target = new HashMap<String, Object>();
        updater.update(target, merced);
        assertEquals(merced, target.get("address"));
    }

    @Test
    public void testSerialization() {
        SimplePofContext ctx = new SimplePofContext();
        ctx.registerUserType(1, MapUpdater.class, new PortableObjectSerializer(1));

        MapUpdater expected = new MapUpdater("address");
        Binary bin = ExternalizableHelper.toBinary(expected, ctx);
        MapUpdater actual = (MapUpdater) ExternalizableHelper.fromBinary(bin, ctx);

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
        assertEquals(expected.toString(), actual.toString());
    }
}
