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

package com.seovic.integration.riak;


import com.basho.riak.pbc.KeySource;
import com.basho.riak.pbc.RiakClient;

import com.google.protobuf.ByteString;

import com.seovic.loader.CsvToCoherence;
import com.seovic.loader.Loader;
import com.seovic.test.objects.Country;

import com.tangosol.io.Serializer;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import com.tangosol.util.processor.PreloadRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author ic  2009.06.15
 */
@SuppressWarnings({"unchecked"})
public class RiakCacheStoreTest {
    public static final ByteString BUCKET = ByteString.copyFromUtf8("test_bucket");
    private RiakClient client;

    public RiakCacheStoreTest()
            throws IOException {
        client = new RiakClient("localhost", 8087);
    }

    @Before
    @After
    public void clearDB()
            throws IOException {
        KeySource keys = client.listKeys(BUCKET);
        Iterator<ByteString> it = keys.iterator();
        while (it.hasNext()) {
            client.delete(BUCKET, it.next());
        }
    }

    @Test
    public void testRiakCacheStore()
            throws IOException {
        NamedCache countries1 = CacheFactory.getCache("countries-1");

        Loader loader = new CsvToCoherence("countries.csv", countries1, Country.class);
        long start = System.currentTimeMillis();
        loader.load();
        long duration = System.currentTimeMillis() - start;
        System.out.println("Written " + countries1.size() + " objects to Riak in " + duration + " ms");
        assertEquals(244, countries1.size());

        start = System.currentTimeMillis();
        List keys = getBucketKeys(countries1.getCacheService().getSerializer());
        duration = System.currentTimeMillis() - start;
        System.out.println("Retrieved " + keys.size() + " bucket keys in " + duration + " ms");

        NamedCache countries2 = CacheFactory.getCache("countries-2");
        start = System.currentTimeMillis();
        countries2.invokeAll(keys, new PreloadRequest());
        duration = System.currentTimeMillis() - start;
        System.out.println("Loaded " + countries2.size() + " objects from Riak in " + duration + " ms");
        assertEquals(244, countries2.size());
    }

    private List getBucketKeys(Serializer serializer)
            throws IOException {
        ArrayList keys = new ArrayList<String>(250);

        KeySource keySource = client.listKeys(BUCKET);
        Iterator<ByteString> it = keySource.iterator();
        while (it.hasNext()) {
            Binary binKey = new Binary(it.next().toByteArray());
            Object key = ExternalizableHelper.fromBinary(binKey, serializer);
            keys.add(key);
        }
        return keys;
    }
}
