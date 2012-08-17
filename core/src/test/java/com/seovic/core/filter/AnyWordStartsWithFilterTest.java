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

package com.seovic.core.filter;


import com.seovic.core.extractor.PropertyExtractor;
import com.seovic.test.objects.Country;
import com.tangosol.io.DefaultSerializer;
import com.tangosol.io.pof.PortableObjectSerializer;
import com.tangosol.io.pof.SimplePofContext;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2011.04.19
 */
public class AnyWordStartsWithFilterTest
    {
    private static NamedCache countries = CacheFactory.getCache("countries");

    @SuppressWarnings({"unchecked"})
    @Before
    public void createTestData()
        {
        countries.clear();

        countries.put("CAF", new Country("CAF", "Central African Republic"));
        countries.put("CZE", new Country("CZE", "Czech Republic"));
        countries.put("KOR", new Country("KOR", "Republic of Korea"));
        countries.put("GBR", new Country("GBR", "United Kingdom"));
        countries.put("USA", new Country("USA", "United States of America"));

        countries.addIndex(new PropertyExtractor("name"), true, null);
        }

    @Test
    public void testCaseSensitiveEvaluation()
        {
        Country caf = new Country("CAF", "Central African Republic");
        Country kor = new Country("KOR", "Republic of Korea");

        assertTrue(new AnyWordStartsWithFilter("name", "Rep", false).evaluate(caf));
        assertFalse(new AnyWordStartsWithFilter("name", "REP", false).evaluate(caf));
        assertFalse(new AnyWordStartsWithFilter("name", "rep", false).evaluate(caf));

        assertTrue(new AnyWordStartsWithFilter("name", "Rep", false).evaluate(kor));
        assertFalse(new AnyWordStartsWithFilter("name", "REP", false).evaluate(kor));
        assertFalse(new AnyWordStartsWithFilter("name", "rep", false).evaluate(kor));              
        }

    @Test
    public void testCaseInsensitiveEvaluation()
        {
        Country caf = new Country("CAF", "Central African Republic");
        Country kor = new Country("KOR", "Republic of Korea");

        assertTrue(new AnyWordStartsWithFilter("name", "Rep", true).evaluate(caf));
        assertTrue(new AnyWordStartsWithFilter("name", "REP", true).evaluate(caf));
        assertTrue(new AnyWordStartsWithFilter("name", "rep", true).evaluate(caf));

        assertTrue(new AnyWordStartsWithFilter("name", "Rep", true).evaluate(kor));
        assertTrue(new AnyWordStartsWithFilter("name", "REP", true).evaluate(kor));
        assertTrue(new AnyWordStartsWithFilter("name", "rep", true).evaluate(kor));
        }

    @Test
    public void testDefaultSerialization()
        {
        Object original = new AnyWordStartsWithFilter("name", "s", true);
        Binary binary   = ExternalizableHelper.toBinary(original, new DefaultSerializer());
        Object copy     = ExternalizableHelper.fromBinary(binary, new DefaultSerializer());

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.toString(), copy.toString());
        }

    @Test
    public void testPofSerialization()
        {
        SimplePofContext ctx = new SimplePofContext();
        ctx.registerUserType(1, AnyWordStartsWithFilter.class, new PortableObjectSerializer(1));
        ctx.registerUserType(2, PropertyExtractor.class, new PortableObjectSerializer(2));

        Object original = new AnyWordStartsWithFilter("name", "s", false);
        Binary binary   = ExternalizableHelper.toBinary(original, ctx);
        Object copy     = ExternalizableHelper.fromBinary(binary, ctx);

        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.toString(), copy.toString());
        }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void testEquals()
        {
        Object o = new AnyWordStartsWithFilter("name", "s", true);

        assertTrue(o.equals(o));
        assertFalse(o.equals(null));
        assertFalse(o.equals("invalid class"));
        assertFalse(o.equals(new AnyWordStartsWithFilter("code", "S", true)));
        }
    }
