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


import com.basho.riak.client.IRiakObject;
import com.basho.riak.client.builders.RiakObjectBuilder;
import com.basho.riak.client.http.util.Constants;
import com.basho.riak.client.raw.RawClient;
import com.basho.riak.client.raw.RiakResponse;
import com.basho.riak.client.raw.pbc.PBClientConfig;
import com.basho.riak.client.raw.pbc.PBRiakClientFactory;

import com.seovic.core.persistence.AbstractBinaryEntryStore;

import com.tangosol.net.CacheFactory;
import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;

import java.io.IOException;

import org.apache.commons.io.Charsets;


/**
 * @author Aleksandar Seovic  2012.08.19
 */
public class RiakCacheStore extends AbstractBinaryEntryStore {
    private RawClient client;
    private String bucket;

    public RiakCacheStore(String bucket, String host, int port)
            throws IOException {
        this.bucket = bucket;
        this.client = PBRiakClientFactory.getInstance().newClient(
                new PBClientConfig.Builder()
                    .withHost(host)
                    .withPort(port)
                    .build());
    }

    @Override
    public void load(BinaryEntry entry) {
        try {
            String key = createRiakKey(entry);
            RiakResponse response = client.fetch(bucket, key);
            if (response.hasValue()) {
                IRiakObject value = response.getRiakObjects()[0];
                entry.updateBinaryValue(new Binary(value.getValue()));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(BinaryEntry entry) {
        try {
            String key = createRiakKey(entry);
            IRiakObject value = RiakObjectBuilder.newBuilder(bucket, key)
                    .withContentType(Constants.CTYPE_OCTET_STREAM)
                    .withValue(entry.getBinaryValue().toByteArray())
                    .build();

            client.store(value);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void erase(BinaryEntry entry) {
        try {
            String key = createRiakKey(entry);
            client.delete(bucket, key);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String createRiakKey(BinaryEntry entry) {
        return entry.getKey().toString();
    }
}
