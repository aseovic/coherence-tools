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


import com.basho.riak.pbc.RiakClient;
import com.basho.riak.pbc.RiakObject;

import com.google.protobuf.ByteString;

import com.seovic.core.persistence.AbstractBinaryEntryStore;

import com.tangosol.util.Binary;
import com.tangosol.util.BinaryEntry;

import java.io.IOException;


/**
 * A cache store implementation that uses <a href="http://basho.com">Riak</a> as a persistent storage.
 *
 * @author Aleksandar Seovic  2012.08.19
 */
public class RiakCacheStore extends AbstractBinaryEntryStore {
    public static final String DEFAULT_HOST = "localhost";
    public static final int    DEFAULT_PORT = 8087;

    private final RiakClient client;
    private final ByteString bucket;

    /**
     * Construct RiakCacheStore instance.
     *
     * @param bucket  Riak bucket this instance should use
     *
     * @throws IOException  if unable to connect to Riak node
     */
    public RiakCacheStore(String bucket)
            throws IOException {
        this(bucket, DEFAULT_HOST, DEFAULT_PORT);
    }

    /**
     * Construct RiakCacheStore instance.
     *
     * @param bucket  Riak bucket this instance should use
     * @param host    Riak host
     * @param port    Riak Protocol Buffers port
     *
     * @throws IOException  if unable to connect to Riak node
     */
    public RiakCacheStore(String bucket, String host, int port)
            throws IOException {
        this.bucket = ByteString.copyFromUtf8(bucket);
        this.client = new RiakClient(host, port);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void load(BinaryEntry entry) {
        try {
            ByteString key = getRiakKey(entry);
            RiakObject[] response = client.fetch(bucket, key);
            if (response.length >= 1) {
                entry.updateBinaryValue(new Binary(response[0].getValue().toByteArray()));
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void store(BinaryEntry entry) {
        try {
            ByteString key = getRiakKey(entry);
            ByteString value = ByteString.copyFrom(entry.getBinaryValue().toByteArray());

            client.store(new RiakObject(bucket, key, value));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void erase(BinaryEntry entry) {
        try {
            ByteString key = getRiakKey(entry);
            client.delete(bucket, key);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert Binary key to ProtoBuf ByteString expected by RiakClient.
     */
    protected ByteString getRiakKey(BinaryEntry entry) {
        return ByteString.copyFrom(entry.getBinaryKey().toByteArray());
    }
}
