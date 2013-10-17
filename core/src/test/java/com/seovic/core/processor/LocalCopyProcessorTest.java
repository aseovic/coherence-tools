package com.seovic.core.processor;


import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

import com.tangosol.util.Filter;
import com.tangosol.util.extractor.IdentityExtractor;
import com.tangosol.util.filter.InFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.util.Set;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2013.09.05
 */
public class LocalCopyProcessorTest
    {
    private static NamedCache SOURCE = CacheFactory.getCache("source");
    private static NamedCache TARGET = CacheFactory.getCache("target");

    @BeforeClass
    public static void populateSource()
        {
        SOURCE.addIndex(IdentityExtractor.INSTANCE, false, null);
        Map<Integer, String> data = new HashMap<Integer, String>(1000);
        for (int i = 1; i <= 1000; i++)
            {
             data.put(i, Integer.toString(i));
            }
        SOURCE.putAll(data);
        }

    @Before
    public void setup()
        {
        TARGET.clear();
        }

    @Test
    public void testSingleEntryCopy()
        {
        SOURCE.invoke(5, new LocalCopyProcessor("target"));
        assertEquals(1, TARGET.size());
        assertEquals("5", TARGET.get(5));
        }

    @Test
    public void testKeySetCopy()
        {
        Set<Integer> keys = new HashSet<Integer>(Arrays.asList(5, 50, 500));
        SOURCE.invokeAll(keys, new LocalCopyProcessor("target"));
        assertEquals(3, TARGET.size());
        assertEquals("5", TARGET.get(5));
        assertEquals("50", TARGET.get(50));
        assertEquals("500", TARGET.get(500));
        }

    @Test
    public void testFilterCopy()
        {
        Set<String> values = new HashSet<String>(Arrays.asList("5", "50", "500"));
        SOURCE.invokeAll(new InFilter(IdentityExtractor.INSTANCE, values),
                         new LocalCopyProcessor("target"));
        assertEquals(3, TARGET.size());
        assertEquals("5", TARGET.get(5));
        assertEquals("50", TARGET.get(50));
        assertEquals("500", TARGET.get(500));
        }

    @Test
    public void testFullCopy()
        {
        SOURCE.invokeAll((Filter) null, new LocalCopyProcessor("target"));
        assertEquals(1000, TARGET.size());
        assertEquals("1", TARGET.get(1));
        assertEquals("5", TARGET.get(5));
        assertEquals("50", TARGET.get(50));
        assertEquals("500", TARGET.get(500));
        assertEquals("1000", TARGET.get(1000));
        }

    }
