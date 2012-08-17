/*
Copyright 2009 Aleksandar Seovic

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
import com.tangosol.util.Filter;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Tests for StartsWithFilter class.
 * 
 * @author Aleksandar Seovic  2009.06.07
 */
@SuppressWarnings("unchecked")
public class StartsWithFilterTest
    {
    private static NamedCache countries = CacheFactory.getCache("countries");

    @Before
    public void createTestData()
        {
        countries.clear();
        
        countries.put("SRB", new Country("SRB", "Serbia"));
        countries.put("SUI", new Country("SUI", "Switzerland"));
        countries.put("SWE", new Country("SWE", "Sweden"));
        countries.put("ESP", new Country("ESP", "Spain"));
        countries.put("USA", new Country("USA", "United States"));

        countries.addIndex(new PropertyExtractor("name"), true, null);
        }

    @Test
    public void testCaseSensitiveEvaluation()
        {
        Country country = new Country("SRB", "Serbia");

        assertTrue(new StartsWithFilter("name", "Ser", false).evaluate(country));
        assertFalse(new StartsWithFilter("name", "ser", false).evaluate(country));
        assertFalse(new StartsWithFilter("name", "SER", false).evaluate(country));
        assertFalse(new StartsWithFilter("name", "Srb", false).evaluate(country));
        }

    @Test
    public void testCaseInsensitiveEvaluation()
        {
        Country country = new Country("SRB", "Serbia");

        assertTrue(new StartsWithFilter("name", "Ser", true).evaluate(country));
        assertTrue(new StartsWithFilter("name", "ser", true).evaluate(country));
        assertTrue(new StartsWithFilter("name", "SER", true).evaluate(country));
        assertFalse(new StartsWithFilter("name", "Srb", true).evaluate(country));
        }

    @Test
    public void testCaseSensitiveEvaluationWithIndex()
        {
        Filter filter = new StartsWithFilter("name", "Sw", false);

        Set keys = countries.keySet(filter);
        assertEquals(2, keys.size());
        assertTrue(keys.contains("SUI"));
        assertTrue(keys.contains("SWE"));
        }

    @Test
    public void testCaseInsensitiveEvaluationWithIndex()
        {
        Filter filter = new StartsWithFilter("name", "s", true);

        Set keys = countries.keySet(filter);
        assertEquals(4, keys.size());
        assertTrue(keys.contains("SRB"));
        assertTrue(keys.contains("SUI"));
        assertTrue(keys.contains("SWE"));
        assertTrue(keys.contains("ESP"));
        }

    @Test
    public void testEvaluationWithMissingIndex()
        {
        Filter filter = new StartsWithFilter("code", "S", false);

        Set keys = countries.keySet(filter);
        assertEquals(3, keys.size());
        assertTrue(keys.contains("SRB"));
        assertTrue(keys.contains("SUI"));
        assertTrue(keys.contains("SWE"));
        }

    @Test
    public void testDefaultSerialization()
        {
        Object original = new StartsWithFilter("name", "s", true);
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
        ctx.registerUserType(1, StartsWithFilter.class, new PortableObjectSerializer(1));
        ctx.registerUserType(2, PropertyExtractor.class, new PortableObjectSerializer(2));

        Object original = new StartsWithFilter("name", "s", false);
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
        Object o = new StartsWithFilter("name", "s", true);

        assertTrue(o.equals(o));
        assertFalse(o.equals(null));
        assertFalse(o.equals("invalid class"));
        assertFalse(o.equals(new StartsWithFilter("code", "S", true)));
        }
    }