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


import com.basho.riak.client.raw.RawClient;
import com.basho.riak.client.raw.pbc.PBClientConfig;
import com.basho.riak.client.raw.pbc.PBRiakClientFactory;
import com.seovic.loader.CsvToCoherence;
import com.seovic.loader.Loader;
import com.seovic.test.objects.Country;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;

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
    public static final String BUCKET = "test_bucket";
    private RawClient client;

    public RiakCacheStoreTest()
            throws IOException {
        client = PBRiakClientFactory.getInstance().newClient(
                new PBClientConfig.Builder()
                    .withHost("192.168.1.23")
                    .withPort(1087)
                    .build());
    }

    @After
    public void clearDB()
            throws IOException {
        Iterable<String> keys = client.listKeys(BUCKET);
        for (String key : keys) {
            client.delete(BUCKET, key);
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
        List keys = getBucketKeys(client, BUCKET);
        duration = System.currentTimeMillis() - start;
        System.out.println("Retrieved " + keys.size() + " bucket keys in " + duration + " ms");

        NamedCache countries2 = CacheFactory.getCache("countries-2");
        start = System.currentTimeMillis();
        countries2.invokeAll(keys, new PreloadRequest());
        duration = System.currentTimeMillis() - start;
        System.out.println("Loaded " + countries2.size() + " objects from Riak in " + duration + " ms");
        assertEquals(244, countries2.size());
    }

    private List getBucketKeys(RawClient client, String bucket)
            throws IOException {
        ArrayList<String> result = new ArrayList<String>(250);
        Iterable<String> keys = client.listKeys(bucket);
        for (String key : keys) {
            result.add(key);
        }
        return result;
    }
}
