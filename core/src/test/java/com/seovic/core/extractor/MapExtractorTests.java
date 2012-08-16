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

package com.seovic.core.extractor;

import static org.junit.Assert.*;

import org.junit.Test;

import com.seovic.test.objects.Address;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;


/**
 * Tests for {@link MapExtractor}
 * 
 * @author ic  2009.06.16
 */
public class MapExtractorTests
    {
    @Test
    public void testWithBadProperty()
        {
        MapExtractor        ext    = new MapExtractor("bad");
        Map<String, Object> target = createTestTarget();
        assertNull(ext.extract(target));
        }

    @Test
    public void testWithNullProperty()
        {
        MapExtractor        ext    = new MapExtractor(null);
        Map<String, Object> target = createTestTarget();
        assertNull(ext.extract(target));
        }

    @Test
    public void testWithNullTarget()
        {
        MapExtractor extractor = new MapExtractor("prop");
        assertNull(extractor.extract(null));
        }

    @Test
    public void testWithExistingProperty()
        {
        MapExtractor        ext     = new MapExtractor("address");
        Address             address = new Address("Merced", "Santiago", "Chile");
        Map<String, Object> target  = createTestTarget();
        assertEquals(address, ext.extract(target));
        ext = new MapExtractor("dob");
        assertTrue(ext.extract(target) instanceof Date);
        }


    // ---- helper methods --------------------------------------------------

    private Map<String, Object> createTestTarget()
        {
        Map<String, Object> target = new HashMap<String, Object>();
        target.put("name", "Ivan");
        target.put("idNo", 2504);
        target.put("dob", new Date());
        target.put("address", new Address("Merced", "Santiago", "Chile"));
        return target;
        }
    }
