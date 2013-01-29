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
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.littlegrid.ClusterMemberGroup;
import org.littlegrid.ClusterMemberGroupUtils;

import static org.junit.Assert.*;


/**
 * @author Aleksandar Seovic  2012.09.20
 */
@SuppressWarnings({"unchecked"})
public class RiakBinaryStoreTest {
    public static final String RIAK_BACKED_COUNTRIES = "riak-backed-countries";
    public static final ByteString BUCKET = ByteString.copyFromUtf8(RIAK_BACKED_COUNTRIES);
    private static ClusterMemberGroup memberGroup;

    private final RiakClient client;
    private final NamedCache countries;

    public RiakBinaryStoreTest() throws IOException {
        client = new RiakClient("localhost", 8087);
        countries = CacheFactory.getCache(RIAK_BACKED_COUNTRIES);
    }

    /**
     * Use BeforeClass to start the cluster up before any of the tests run - this ensures
     * we only have the start-up delay only once.
     *
     * Note: apart from starting up and shutting down littlegrid, its code and API shouldn't
     * really be in any of your tests, unless you want to perform a stop or shutdown of a
     * particular member for failover testing.
     */
    @BeforeClass
    public static void beforeTests() {
        memberGroup = ClusterMemberGroupUtils.newBuilder()
                .setStorageEnabledCount(2)
                .setFastStartJoinTimeoutMilliseconds(100)
                .buildAndConfigureForStorageDisabledClient();
    }

    /**
     * Shutdown the cluster, this method also does a CacheFactory.shutdown() for the client
     * to ensure that leaves nicely as well.
     */
    @AfterClass
    public static void afterTests() {
        ClusterMemberGroupUtils.shutdownCacheFactoryThenClusterMemberGroups(memberGroup);
    }

    @Before
    public void clearDB() {
        countries.clear();
    }

    @Test
    public void testRiakBinaryStore()
            throws IOException {
        Loader loader = new CsvToCoherence("countries.csv", countries, Country.class);
        long start = System.currentTimeMillis();
        loader.load();
        long duration = System.currentTimeMillis() - start;
        System.out.println("Stored " + countries.size() + " objects to Riak in " + duration + " ms");
        assertEquals(244, countries.size());

        start = System.currentTimeMillis();
        Set<String> keys = countries.keySet();
        duration = System.currentTimeMillis() - start;
        System.out.println("Retrieved " + keys.size() + " keys in " + duration + " ms");

        start = System.currentTimeMillis();
        for (String key : keys) {
            Country country = (Country) countries.get(key);
            System.out.println(country);
        }
        duration = System.currentTimeMillis() - start;
        System.out.println("Loaded " + countries.size() + " objects from Riak in " + duration + " ms");
    }
}
